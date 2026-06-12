package com.smartstay.application_mobile_frontend.feature.iam.data.di

import com.smartstay.application_mobile_frontend.feature.iam.data.remote.IamApiService
import com.smartstay.application_mobile_frontend.feature.iam.data.repositories.IamRepositoryImpl
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IamRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindIamRepository(impl: IamRepositoryImpl): IamRepository
}

@Module
@InstallIn(SingletonComponent::class)
object IamApiModule {

    @Provides
    @Singleton
    fun provideIamApiService(retrofit: Retrofit): IamApiService {
        return retrofit.create(IamApiService::class.java)
    }
}