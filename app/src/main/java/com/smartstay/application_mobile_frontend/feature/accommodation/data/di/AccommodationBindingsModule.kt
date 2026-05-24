package com.smartstay.application_mobile_frontend.feature.accommodation.data.di

import com.smartstay.application_mobile_frontend.feature.accommodation.data.repositories.HotelRepositoryImpl
import com.smartstay.application_mobile_frontend.feature.accommodation.domain.repository.HotelRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AccommodationBindingsModule {

    @Binds
    @Singleton
    abstract fun bindHotelRepository(
        implementation: HotelRepositoryImpl
    ): HotelRepository
}
