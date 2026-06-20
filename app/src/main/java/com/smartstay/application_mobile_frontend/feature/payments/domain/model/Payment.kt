package com.smartstay.application_mobile_frontend.feature.payments.domain.model

import java.time.LocalDateTime

data class Payment(
    val id: Int,
    val bookingId: Int,
    val transactionId: String,
    val amount: Double,
    val currency: String,
    val method: PaymentMethod,
    val provider: PaymentProvider,
    val cardHolderName: String?,
    val cardNumberMasked: String?,
    val paymentDate: LocalDateTime?,
    val status: PaymentStatus
)
