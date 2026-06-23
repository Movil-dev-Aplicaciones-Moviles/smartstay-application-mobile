package com.smartstay.application_mobile_frontend.feature.payments.data.repositories

import com.smartstay.application_mobile_frontend.feature.payments.data.mapper.toDomain
import com.smartstay.application_mobile_frontend.feature.payments.data.mapper.toDto
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.PaymentApiService
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.CreatePaymentCommand
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.Payment
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.PaymentCheckout
import com.smartstay.application_mobile_frontend.feature.payments.domain.repository.PaymentRepository
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(
    private val apiService: PaymentApiService
) : PaymentRepository {

    override suspend fun createPayment(command: CreatePaymentCommand): Result<Payment> {
        return try {
            Result.success(apiService.createPayment(command.toDto()).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createMercadoPagoCheckout(
        command: CreatePaymentCommand
    ): Result<PaymentCheckout> {
        return try {
            Result.success(apiService.createMercadoPagoCheckout(command.toDto()).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPaymentById(id: Int): Result<Payment> {
        return try {
            Result.success(apiService.getPaymentById(id).toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPaymentsByBookingId(bookingId: Int): Result<List<Payment>> {
        return try {
            val payments = apiService.getPaymentsByBookingId(bookingId).map { it.toDomain() }
            Result.success(payments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
