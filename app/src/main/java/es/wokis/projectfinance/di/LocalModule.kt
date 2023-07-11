package es.wokis.projectfinance.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import es.wokis.projectfinance.data.constants.AppConstants.SHARED_PREFERENCES_NAME
import es.wokis.projectfinance.data.dao.category.CategoryDAO
import es.wokis.projectfinance.data.dao.invoice.InvoiceDAO
import es.wokis.projectfinance.data.dao.reaction.ReactionDAO
import es.wokis.projectfinance.data.database.AppDatabase
import es.wokis.projectfinance.data.database.AppDatabase.Companion.MIGRATION_1000002_1000003
import es.wokis.projectfinance.data.database.AppDatabase.Companion.MIGRATION_1000003_1000004
import es.wokis.projectfinance.data.datasource.category.CategoryLocalDataSource
import es.wokis.projectfinance.data.datasource.invoice.InvoiceLocalDataSource
import es.wokis.projectfinance.data.datasource.reaction.ReactionLocalDataSource
import es.wokis.projectfinance.data.local.category.CategoryLocalDataSourceImpl
import es.wokis.projectfinance.data.local.invoice.InvoiceLocalDataSourceImpl
import es.wokis.projectfinance.data.local.reaction.ReactionLocalDataSourceImpl
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LocalModule {
    @Provides
    @Singleton
    fun provideRoom(@ApplicationContext context: Context) = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "project-finance-database"
    )
        .addMigrations(MIGRATION_1000002_1000003)
        .addMigrations(MIGRATION_1000003_1000004)
//        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideInvoiceDAO(database: AppDatabase): InvoiceDAO = database.getInvoiceDAO()

    @Provides
    @Singleton
    fun provideCategoryDAO(database: AppDatabase): CategoryDAO = database.getCategoryDAO()

    @Provides
    @Singleton
    fun provideReactionDAO(database: AppDatabase): ReactionDAO = database.getReactionDAO()

    @Provides
    @Singleton
    fun provideInvoiceLocalDataSource(invoiceDAO: InvoiceDAO): InvoiceLocalDataSource =
        InvoiceLocalDataSourceImpl(invoiceDAO)

    @Provides
    @Singleton
    fun provideCategoryLocalDataSource(categoryDAO: CategoryDAO): CategoryLocalDataSource =
        CategoryLocalDataSourceImpl(categoryDAO)

    @Provides
    @Singleton
    fun provideReactionLocalDataSource(reactionDAO: ReactionDAO): ReactionLocalDataSource =
        ReactionLocalDataSourceImpl(reactionDAO)
}