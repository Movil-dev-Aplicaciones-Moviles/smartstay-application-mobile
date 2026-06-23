package com.smartstay.application_mobile_frontend.feature.payments.data.di

import com.smartstay.application_mobile_frontend.feature.payments.data.remote.MercadoPagoApiService
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.PaymentApiService
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.PaymentCheckoutDetailsApiService
import com.smartstay.application_mobile_frontend.feature.payments.data.repositories.PaymentRepositoryImpl
import com.smartstay.application_mobile_frontend.feature.payments.domain.repository.PaymentRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PaymentRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        implementation: PaymentRepositoryImpl
    ): PaymentRepository
}

@Module
@InstallIn(SingletonComponent::class)
object PaymentApiModule {
    @Provides
    @Singleton
    fun provideMercadoPagoApiService(): MercadoPagoApiService {
        return Retrofit.Builder()
            .baseUrl("https://api.mercadopago.com/")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()
            .create(MercadoPagoApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentApiService(retrofit: Retrofit): PaymentApiService {
        return retrofit.create(PaymentApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentCheckoutDetailsApiService(retrofit: Retrofit): PaymentCheckoutDetailsApiService {
        return retrofit.create(PaymentCheckoutDetailsApiService::class.java)
    }
}
