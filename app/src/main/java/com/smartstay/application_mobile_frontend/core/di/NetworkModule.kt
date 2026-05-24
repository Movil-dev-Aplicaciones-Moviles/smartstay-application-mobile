// core/di/NetworkModule.kt
package com.smartstay.application_mobile_frontend.core.di

import com.smartstay.application_mobile_frontend.core.network.AuthInterceptor
import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.AccommodationApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://application-mobile-backend.onrender.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAccommodationApiService(retrofit: Retrofit): AccommodationApiService {
        return retrofit.create(AccommodationApiService::class.java)
    }
}