package com.smartstay.application_mobile_frontend.feature.payments.domain.repository

import com.smartstay.application_mobile_frontend.feature.payments.domain.model.CreatePaymentCommand
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.Payment
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.PaymentCheckout

interface PaymentRepository {
    suspend fun createPayment(command: CreatePaymentCommand): Result<Payment>
    suspend fun createMercadoPagoCheckout(command: CreatePaymentCommand): Result<PaymentCheckout>
    suspend fun getPaymentById(id: Int): Result<Payment>
    suspend fun getPaymentsByBookingId(bookingId: Int): Result<List<Payment>>
}
