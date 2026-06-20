// feature/booking/data/di/BookingBindingsModule.kt
package com.smartstay.application_mobile_frontend.feature.booking.data.di

import com.smartstay.application_mobile_frontend.domain.repository.bookings.BookingRepository
import com.smartstay.application_mobile_frontend.feature.booking.data.remote.BookingApiService
import com.smartstay.application_mobile_frontend.feature.booking.data.repositories.BookingRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class `BookingBindingsModule.kt` {

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        implementation: BookingRepositoryImpl
    ): BookingRepository

    companion object {

        @Provides
        @Singleton
        fun provideBookingApiService(retrofit: Retrofit): BookingApiService {
            return retrofit.create(BookingApiService::class.java)
        }
    }
}