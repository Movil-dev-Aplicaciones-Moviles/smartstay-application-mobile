package com.smartstay.application_mobile_frontend.feature.iam.data.di

import com.smartstay.application_mobile_frontend.feature.iam.data.remote.IamApiService
import com.smartstay.application_mobile_frontend.feature.iam.data.repository.IamRepositoryImpl
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IamBindingsModule {

    /**
     * Proveedor de [IamApiService].
     * Crea la implementación Retrofit para el servicio IAM.
     *
     * @param retrofit Instancia de Retrofit provista por [com.smartstay.application_mobile_frontend.core.di.NetworkModule].
     */
    @Provides
    @Singleton
    fun provideIamApiService(retrofit: Retrofit): IamApiService {
        return retrofit.create(IamApiService::class.java)
    }

    @Provides
    @Singleton
    fun bindIamRepository(impl: IamRepositoryImpl): IamRepository = impl
}
