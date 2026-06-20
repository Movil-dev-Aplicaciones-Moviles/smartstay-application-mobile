package com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto

data class PaymentDto(
    val id: Int,
    val bookingId: Int,
    val transactionId: String?,
    val amount: Double,
    val currency: String?,
    val method: String?,
    val provider: String?,
    val cardHolderName: String?,
    val cardNumberMasked: String?,
    val paymentDate: String?,
    val status: String?
)
