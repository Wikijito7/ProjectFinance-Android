package es.wokis.projectfinance.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.wokis.projectfinance.data.datasource.category.CategoryLocalDataSource
import es.wokis.projectfinance.data.datasource.invoice.InvoiceLocalDataSource
import es.wokis.projectfinance.data.datasource.invoice.InvoiceRemoteDataSource
import es.wokis.projectfinance.data.datasource.reaction.ReactionLocalDataSource
import es.wokis.projectfinance.data.datasource.totp.TotpRemoteDataSource
import es.wokis.projectfinance.data.datasource.user.UserRemoteDataSource
import es.wokis.projectfinance.data.error.RepositoryErrorManager
import es.wokis.projectfinance.data.repository.category.CategoryRepository
import es.wokis.projectfinance.data.repository.category.CategoryRepositoryImpl
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepository
import es.wokis.projectfinance.data.repository.invoice.InvoiceRepositoryImpl
import es.wokis.projectfinance.data.repository.reaction.ReactionRepository
import es.wokis.projectfinance.data.repository.reaction.ReactionRepositoryImpl
import es.wokis.projectfinance.data.repository.totp.TotpRepository
import es.wokis.projectfinance.data.repository.totp.TotpRepositoryImpl
import es.wokis.projectfinance.data.repository.user.UserRepository
import es.wokis.projectfinance.data.repository.user.UserRepositoryImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun provideRepositoryErrorManager(sharedPreferences: SharedPreferences): RepositoryErrorManager =
        RepositoryErrorManager(sharedPreferences)

    @Provides
    @Singleton
    fun provideInvoiceRepository(
        invoiceDataSource: InvoiceLocalDataSource,
        reactionLocalDataSource: ReactionLocalDataSource,
        invoiceRemoteDataSource: InvoiceRemoteDataSource,
        userRepository: UserRepository,
        categoryRepository: CategoryRepository,
        sharedPreferences: SharedPreferences,
        repositoryErrorManager: RepositoryErrorManager
    ): InvoiceRepository =
        InvoiceRepositoryImpl(
            invoiceDataSource,
            reactionLocalDataSource,
            invoiceRemoteDataSource,
            userRepository,
            categoryRepository,
            sharedPreferences,
            repositoryErrorManager
        )

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryLocalDataSource: CategoryLocalDataSource): CategoryRepository =
        CategoryRepositoryImpl(categoryLocalDataSource)

    @Provides
    @Singleton
    fun provideUserRepository(
        userRemoteDataSource: UserRemoteDataSource,
        sharedPreferences: SharedPreferences,
        repositoryErrorManager: RepositoryErrorManager
    ): UserRepository =
        UserRepositoryImpl(userRemoteDataSource, sharedPreferences, repositoryErrorManager)

    @Provides
    @Singleton
    fun provideReactionRepository(
        localDataSource: ReactionLocalDataSource,
        invoiceRepository: InvoiceRepository
    ): ReactionRepository =
        ReactionRepositoryImpl(localDataSource, invoiceRepository)

    @Provides
    @Singleton
    fun provideTotpRepository(
        remoteDataSource: TotpRemoteDataSource,
        repositoryErrorManager: RepositoryErrorManager
    ): TotpRepository =
        TotpRepositoryImpl(remoteDataSource, repositoryErrorManager)
}