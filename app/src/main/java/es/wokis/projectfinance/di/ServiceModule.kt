package es.wokis.projectfinance.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import es.wokis.projectfinance.BuildConfig
import es.wokis.projectfinance.data.constants.AppConstants.EMPTY_TEXT
import es.wokis.projectfinance.data.constants.AppConstants.TIMESTAMP_HEADER
import es.wokis.projectfinance.data.constants.AppConstants.TOTP
import es.wokis.projectfinance.data.constants.AppConstants.TOTP_HEADER
import es.wokis.projectfinance.data.constants.AppConstants.TOTP_TIMESTAMP
import es.wokis.projectfinance.data.constants.AppConstants.USER_BEARER_TOKEN
import es.wokis.projectfinance.data.constants.WsConstants
import es.wokis.projectfinance.data.remote.invoice.service.InvoiceService
import es.wokis.projectfinance.data.remote.totp.service.TotpService
import es.wokis.projectfinance.data.remote.user.service.UserService
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
class ServiceModule {

    @Provides
    @Singleton
    fun provideUserService(client: OkHttpClient): UserService =
        getAdapter(WsConstants.BASE_URL, client).create(UserService::class.java)

    @Provides
    @Singleton
    fun provideInvoiceService(client: OkHttpClient): InvoiceService =
        getAdapter(WsConstants.BASE_URL, client).create(InvoiceService::class.java)

    @Provides
    @Singleton
    fun provideTotpService(client: OkHttpClient): TotpService =
        getAdapter(WsConstants.BASE_URL, client).create(TotpService::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(sharedPreferences: SharedPreferences): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY

            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder().addInterceptor { chain ->
            val newRequest: Request = chain
                .request()
                .newBuilder()
                .addHeader(
                    "Authorization",
                    "Bearer ${sharedPreferences.getString(USER_BEARER_TOKEN, EMPTY_TEXT)}"
                )
                .addHeader(
                    TOTP_HEADER,
                    sharedPreferences.getString(TOTP, EMPTY_TEXT).orEmpty()
                )
                .addHeader(
                    TIMESTAMP_HEADER,
                    sharedPreferences.getLong(TOTP_TIMESTAMP, 0L).takeIf { it > 0 }?.toString().orEmpty()
                )
                .build()
            chain.proceed(newRequest)
        }.addInterceptor(logInterceptor).build()
    }

    private fun getAdapter(baseUrl: String, client: OkHttpClient) =
        Retrofit.Builder().baseUrl(baseUrl).client(client)
            .addConverterFactory(GsonConverterFactory.create()).build()
}