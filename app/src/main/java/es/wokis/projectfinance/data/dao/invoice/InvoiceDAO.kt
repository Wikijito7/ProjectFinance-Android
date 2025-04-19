package es.wokis.projectfinance.data.dao.invoice

import androidx.room.*
import es.wokis.projectfinance.data.local.invoice.dbo.FullInvoiceDBO
import es.wokis.projectfinance.data.local.invoice.dbo.InvoiceDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDAO {
    @Transaction
    @Query("SELECT * FROM invoice i INNER JOIN category c on i.foreignCategoryId = c.categoryId WHERE (userId IS NULL OR userId = :userId) ORDER BY i.date DESC, i.id DESC")
    fun getAllInvoices(userId: String): Flow<List<FullInvoiceDBO>>

    @Transaction
    @Query("SELECT * FROM invoice i INNER JOIN category c on i.foreignCategoryId = c.categoryId WHERE (userId IS NULL OR userId = :userId) ORDER BY i.date DESC, i.id DESC LIMIT :quantity")
    fun getInvoices(userId: String, quantity: Int): Flow<List<FullInvoiceDBO>>

    @Transaction
    @Query("SELECT * FROM invoice i INNER JOIN category c on i.foreignCategoryId = c.categoryId WHERE (userId IS NULL OR userId = :userId) AND i.date BETWEEN :startDate AND :endDate ORDER BY i.date DESC, i.id DESC")
    fun getInvoicesBetween(userId: String, startDate: Long, endDate: Long): Flow<List<FullInvoiceDBO>>

    @Transaction
    @Query("SELECT * FROM invoice i INNER JOIN category c on i.foreignCategoryId = c.categoryId WHERE i.date = :date ORDER BY i.id DESC")
    fun getInvoicesOn(date: Long): Flow<List<FullInvoiceDBO>>

    @Transaction
    @Query("SELECT * FROM invoice i INNER JOIN category c on i.foreignCategoryId = c.categoryId WHERE i.id = :id")
    fun getInvoiceById(id: Long): FullInvoiceDBO

    @Query("SELECT * FROM invoice WHERE NOT EXISTS(SELECT 1 FROM Category WHERE categoryId = foreignCategoryId)")
    fun getInvoicesWithoutCategory(): List<InvoiceDBO>

    @Transaction
    @Query("SELECT * FROM invoice i INNER JOIN category c on i.foreignCategoryId = c.categoryId WHERE i.serverId IS NULL AND (userId IS NULL OR userId = :userId) and i.synchronize == 1 ORDER BY i.date DESC, i.id DESC")
    fun getInvoicesNotSynchronized(userId: String): List<FullInvoiceDBO>

    @Transaction
    @Query("SELECT * FROM invoice i INNER JOIN category c on i.foreignCategoryId = c.categoryId WHERE i.updated == 1 AND (userId IS NULL OR userId = :userId) and i.synchronize == 1 ORDER BY i.date DESC, i.id DESC")
    fun getInvoicesUpdatedNotSynchronized(userId: String): List<FullInvoiceDBO>

    @Transaction
    @Query("SELECT count(id) FROM invoice i INNER JOIN category c on i.foreignCategoryId = c.categoryId WHERE (userId IS NULL OR userId = :userId)")
    fun getNumberOfInvoices(userId: String): Flow<Int>

    @Transaction
    @Query("SELECT * FROM invoice i INNER JOIN category c on i.foreignCategoryId = c.categoryId WHERE NOT (userId IS NULL OR userId = :userId) ORDER BY i.date DESC, i.id DESC")
    fun getInvoicesOfOtherUser(userId: String): List<FullInvoiceDBO>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addInvoice(invoice: InvoiceDBO): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addInvoices(invoices: List<InvoiceDBO>): List<Long>

    @Query("DELETE FROM invoice where id = :id")
    fun removeInvoice(id: Long): Int

    @Update
    fun updateInvoice(invoice: InvoiceDBO): Int
}