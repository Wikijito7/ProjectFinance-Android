package es.wokis.projectfinance.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.wokis.projectfinance.data.datasource.invoice.InvoiceRemoteDataSource
import es.wokis.projectfinance.data.datasource.totp.TotpRemoteDataSource
import es.wokis.projectfinance.data.datasource.user.UserRemoteDataSource
import es.wokis.projectfinance.data.remote.invoice.InvoiceRemoteDataSourceImpl
import es.wokis.projectfinance.data.remote.invoice.service.InvoiceService
import es.wokis.projectfinance.data.remote.totp.TotpRemoteDataSourceImpl
import es.wokis.projectfinance.data.remote.totp.service.TotpService
import es.wokis.projectfinance.data.remote.user.UserRemoteDataSourceImpl
import es.wokis.projectfinance.data.remote.user.service.UserService
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RemoteModule {
    @Provides
    @Singleton
    fun provideUserRemoteDataSource(service: UserService): UserRemoteDataSource =
        UserRemoteDataSourceImpl(service)

    @Provides
    @Singleton
    fun provideInvoiceRemoteDataSource(service: InvoiceService): InvoiceRemoteDataSource =
        InvoiceRemoteDataSourceImpl(service)

    @Provides
    @Singleton
    fun provideTotpRemoteDataSource(service: TotpService): TotpRemoteDataSource =
        TotpRemoteDataSourceImpl(service)
}