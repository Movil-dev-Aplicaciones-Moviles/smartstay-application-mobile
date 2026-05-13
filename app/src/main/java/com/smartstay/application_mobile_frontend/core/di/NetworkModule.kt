package com.smartstay.application_mobile_frontend.core.di

import com.smartstay.application_mobile_frontend.data.remote.accommodations.AccommodationApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            // replace the link with the url of your backend
            .baseUrl("https://application-mobile-backend.onrender.com/swagger/index.html")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAccommodationApiService(retrofit: Retrofit): AccommodationApiService {
        return retrofit.create(AccommodationApiService::class.java)
    }
}