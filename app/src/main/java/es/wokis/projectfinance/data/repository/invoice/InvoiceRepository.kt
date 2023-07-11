package es.wokis.projectfinance.data.repository.invoice

import android.content.SharedPreferences
import androidx.core.content.edit
import es.wokis.projectfinance.data.bo.category.CategoryBO
import es.wokis.projectfinance.data.bo.invoice.InvoiceBO
import es.wokis.projectfinance.data.constants.AppConstants.CATEGORIES_SEPARATOR
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_CATEGORY
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_CATEGORY_ID
import es.wokis.projectfinance.data.constants.AppConstants.DEFAULT_COLOR
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_CATEGORIES_ID
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_DATE_FROM
import es.wokis.projectfinance.data.constants.AppConstants.FILTER_DATE_TO
import es.wokis.projectfinance.data.datasource.invoice.InvoiceLocalDataSource
import es.wokis.projectfinance.data.datasource.invoice.InvoiceRemoteDataSource
import es.wokis.projectfinance.data.datasource.reaction.ReactionLocalDataSource
import es.wokis.projectfinance.data.error.RepositoryErrorManager
import es.wokis.projectfinance.data.repository.category.CategoryRepository
import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.response.AsyncResult
import es.wokis.projectfinance.utils.isTrue
import es.wokis.projectfinance.utils.orZero
import es.wokis.projectfinance.utils.toDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface InvoiceRepository {
    suspend fun getInvoices(): Flow<List<InvoiceBO>>
    suspend fun getNumberOfInvoices(): Flow<Int>
    suspend fun getLastInvoices(numberOfInvoices: Int): Flow<List<InvoiceBO>>
    suspend fun getInvoiceById(id: Long): InvoiceBO
    suspend fun getInvoicesBetween(startDate: Long, endDate: Long): Flow<List<InvoiceBO>>
    suspend fun getFilteredInvoices(): Flow<List<InvoiceBO>>
    suspend fun addInvoice(invoice: InvoiceBO): Boolean
    suspend fun removeInvoice(invoice: InvoiceBO, synchronize: Boolean = true): Boolean
    suspend fun updateInvoice(invoice: InvoiceBO): Boolean
    suspend fun reinsertRemovedInvoice(): Boolean
    suspend fun checkInvoicesWithoutCategory(): Boolean
    suspend fun synchronizeInvoices(): Flow<AsyncResult<Boolean>>
    suspend fun getInvoicesOfOtherUser(): List<InvoiceBO>
    fun saveFilters(filters: Map<String, String?>)
    fun clearFilters()
}

class InvoiceRepositoryImpl(
    private val localDataSource: InvoiceLocalDataSource,
    private val reactionLocalDataSource: ReactionLocalDataSource,
    private val remoteDataSource: InvoiceRemoteDataSource,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val sharedPreferences: SharedPreferences,
    private val repositoryErrorManager: RepositoryErrorManager
) : InvoiceRepository {

    private var removedInvoice: InvoiceBO? = null

    private var filters: Map<String, String?> = mapOf()

    override suspend fun getInvoices(): Flow<List<InvoiceBO>> =
        localDataSource.getInvoices(getUserId())

    override suspend fun getNumberOfInvoices(): Flow<Int> =
        localDataSource.getNumberOfInvoices(getUserId())

    override suspend fun getLastInvoices(numberOfInvoices: Int): Flow<List<InvoiceBO>> =
        localDataSource.getLastInvoices(getUserId(), numberOfInvoices)

    override suspend fun getInvoiceById(id: Long): InvoiceBO = localDataSource.getInvoiceById(id)

    override suspend fun getInvoicesBetween(startDate: Long, endDate: Long): Flow<List<InvoiceBO>> =
        localDataSource.getInvoicesBetween(getUserId(), startDate, endDate)

    override suspend fun getFilteredInvoices(): Flow<List<InvoiceBO>> {
        val dateFrom = filters[FILTER_DATE_FROM]?.toDate()
        val dateTo = filters[FILTER_DATE_TO]?.toDate()
        val categories =
            filters[FILTER_CATEGORIES_ID]?.split(CATEGORIES_SEPARATOR)?.mapNotNull {
                it.toLongOrNull()
            }.orEmpty()
        val invoicesFlow = dateFrom?.let {
            getInvoicesBetween(it.time, dateTo?.time.orZero())
        } ?: getInvoices()

        return invoicesFlow.applyCategoryFilter(categories)
    }

    override suspend fun addInvoice(invoice: InvoiceBO): Boolean =
        localDataSource.addInvoice(invoice)

    override suspend fun removeInvoice(invoice: InvoiceBO, synchronize: Boolean): Boolean {
        removedInvoice = invoice
        if (synchronize) {
            addRemovedInvoice(invoice)
        }
        return localDataSource.removeInvoice(invoice.id)
    }

    override suspend fun updateInvoice(invoice: InvoiceBO): Boolean =
        localDataSource.updateInvoice(invoice)

    override suspend fun reinsertRemovedInvoice(): Boolean = removedInvoice?.let {
        removeRemovedInvoice(it)
        localDataSource.addInvoice(it)
    }.isTrue()

    override suspend fun checkInvoicesWithoutCategory(): Boolean {
        localDataSource.getInvoicesWithoutCategory().map {
            it.copy(
                category = CategoryBO(
                    DEFAULT_CATEGORY_ID,
                    DEFAULT_CATEGORY,
                    DEFAULT_COLOR
                )
            )
        }.forEach {
            updateInvoice(it)
        }
        return true
    }

    override fun saveFilters(filters: Map<String, String?>) {
        this.filters = filters
    }

    override fun clearFilters() {
        this.filters = mapOf()
    }

    override suspend fun synchronizeInvoices(): Flow<AsyncResult<Boolean>> =
        repositoryErrorManager.wrap {
            getInvoicesNotSynchronized().takeIf { it.isNotEmpty() }?.let {
                remoteDataSource.addInvoices(it).also { acknowledge ->
                    if (acknowledge) {
                        updateInvoices(it.map { invoice -> invoice.copy(serverId = EMPTY_TEXT) })
                    }
                }
            }

            getInvoicesUpdatedNotSynchronized().takeIf { it.isNotEmpty() }?.let {
                remoteDataSource.updateInvoices(it).also { acknowledge ->
                    if (acknowledge) {
                        updateInvoices(it)
                    }
                }
            }

            getRemovedInvoices().toList().takeIf { it.isNotEmpty() }?.let {
                remoteDataSource.deleteInvoices(it).also {
                    clearRemovedInvoices()
                }
            }

            remoteDataSource.getInvoices().takeIf { it.isNotEmpty() }?.let { invoices ->
                invoices.mapNotNull { invoice ->
                    invoice.category
                }.distinctBy { category ->
                    category.id
                }.forEach { category ->
                    categoryRepository.addCategory(category)
                }

                invoices.map { invoice ->
                    invoice.reactions
                }.flatten().forEach { reaction ->
                    reactionLocalDataSource.addReaction(reaction)
                }
                localDataSource.addInvoices(invoices)
            }.isTrue()
        }

    override suspend fun getInvoicesOfOtherUser(): List<InvoiceBO> =
        localDataSource.getInvoicesOfOtherUser(getUserId())

    private fun getUserId() = userRepository.getCurrentUserId().orEmpty()

    private suspend fun updateInvoices(invoices: List<InvoiceBO>) {
        invoices.forEach {
            updateInvoice(it.copy(updated = false))
        }
    }

    private suspend fun getInvoicesNotSynchronized(): List<InvoiceBO> =
        userRepository.getCurrentUserId()?.let {
            localDataSource.getInvoicesNotSynchronized(it)
        }.orEmpty()

    private suspend fun getInvoicesUpdatedNotSynchronized(): List<InvoiceBO> =
        userRepository.getCurrentUserId()?.let {
            localDataSource.getInvoicesUpdatedNotSynchronized(it)
        }.orEmpty()

    private fun Flow<List<InvoiceBO>>.applyCategoryFilter(categories: List<Long>) =
        categories.takeIf { it.isNotEmpty() }?.let { categoriesNotNull ->
            map {
                it.filter { invoice ->
                    categoriesNotNull.contains(invoice.category?.id)
                }
            }
        } ?: this

    private fun addRemovedInvoice(invoice: InvoiceBO) {
        val invoicesIds = getRemovedInvoices()
            .toMutableSet()
            .apply { add(invoice.serverId) }
            .toSet()

        sharedPreferences.edit {
            putStringSet(DELETED_INVOICES_KEY, invoicesIds)
        }
    }

    private fun removeRemovedInvoice(invoice: InvoiceBO) {
        val invoicesIds = getRemovedInvoices()
            .filter { it != invoice.serverId }
            .toSet()

        sharedPreferences.edit {
            putStringSet(DELETED_INVOICES_KEY, invoicesIds)
        }
    }

    private fun clearRemovedInvoices() {
        sharedPreferences.edit {
            putStringSet(DELETED_INVOICES_KEY, emptySet())
        }
    }

    private fun getRemovedInvoices() = sharedPreferences
        .getStringSet(DELETED_INVOICES_KEY, emptySet())
        .orEmpty()

    companion object {
        private const val DELETED_INVOICES_KEY = "DELETED_INVOICES_KEY"
    }
}

