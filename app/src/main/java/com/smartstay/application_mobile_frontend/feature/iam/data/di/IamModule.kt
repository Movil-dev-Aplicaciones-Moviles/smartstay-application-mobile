package com.smartstay.application_mobile_frontend.feature.iam.data.di

import com.smartstay.application_mobile_frontend.feature.iam.data.remote.AuthApiService
import com.smartstay.application_mobile_frontend.feature.iam.data.repository.AuthRepositoryImpl
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}

@Module
@InstallIn(SingletonComponent::class)
object IamApiModule {

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}
