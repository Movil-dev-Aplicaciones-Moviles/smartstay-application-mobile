package com.smartstay.application_mobile_frontend.feature.profile.data.di

import com.smartstay.application_mobile_frontend.feature.profile.data.remote.ProfileApiService
import com.smartstay.application_mobile_frontend.feature.profile.data.repository.ProfileRepositoryImpl
import com.smartstay.application_mobile_frontend.feature.profile.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProfileBindingsModule {

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        implementation: ProfileRepositoryImpl
    ): ProfileRepository

    companion object {
        @Provides
        @Singleton
        fun provideProfileApiService(retrofit: Retrofit): ProfileApiService {
            return retrofit.create(ProfileApiService::class.java)
        }
    }
}