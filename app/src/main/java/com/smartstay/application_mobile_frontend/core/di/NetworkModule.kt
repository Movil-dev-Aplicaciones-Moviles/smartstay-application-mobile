// core/di/NetworkModule.kt
package com.smartstay.application_mobile_frontend.core.di

import com.smartstay.application_mobile_frontend.core.network.AuthInterceptor
import com.smartstay.application_mobile_frontend.feature.options.data.remote.OptionsApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://application-mobile-backend.onrender.com/api/v1/"

    /**
     * Proveedor de OkHttpClient.
     * Incluye el AuthInterceptor para añadir el token JWT a las peticiones
     * y un logging interceptor para depuración en desarrollo.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    /**
     * Proveedor de Retrofit.
     * Recibe el OkHttpClient inyectado por Hilt y configura la URL base
     * con el convertidor Gson para serialización/deserialización JSON.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOptionsApiService(retrofit: Retrofit): OptionsApiService {
        return retrofit.create(OptionsApiService::class.java)
    }
}
