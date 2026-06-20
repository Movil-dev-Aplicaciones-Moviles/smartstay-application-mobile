package com.smartstay.application_mobile_frontend.feature.accommodation.data.di

import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.AccommodationApiService
import com.smartstay.application_mobile_frontend.feature.accommodation.data.repositories.HotelRepositoryImpl
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository.HotelRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AccommodationBindingsModule {

    @Binds
    @Singleton
    abstract fun bindHotelRepository(
        implementation: HotelRepositoryImpl
    ): HotelRepository

    // REMOVED the extra @Module and @InstallIn from here
    companion object {

        @Provides
        @Singleton
        fun provideAccommodationApiService(retrofit: Retrofit): AccommodationApiService {
            return retrofit.create(AccommodationApiService::class.java)
        }
    }
}