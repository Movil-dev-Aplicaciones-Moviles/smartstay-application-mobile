package com.smartstay.application_mobile_frontend.feature.options.data.di

import com.smartstay.application_mobile_frontend.feature.options.data.repositories.OptionsRepositoryImpl
import com.smartstay.application_mobile_frontend.feature.options.domain.repository.OptionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OptionsBindingsModule {

    @Binds
    @Singleton
    abstract fun bindOptionsRepository(
        implementation: OptionsRepositoryImpl
    ): OptionsRepository
}
