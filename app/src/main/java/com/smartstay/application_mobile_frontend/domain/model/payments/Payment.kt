package com.smartstay.application_mobile_frontend.domain.model.payments

import java.time.LocalDateTime

data class Payment(
    val id: Int,
    val bookingId: Int,
    val transactionId: String,
    val amount: Double,
    val paymentMethod: String,
    val cardHolderName: String,
    val cardNumberMasked: String,
    val paymentDate: LocalDateTime,
    val status: PaymentStatus
)