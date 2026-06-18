package com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto

data class CreatePaymentRequestDto(
    val bookingId: Int,
    val amount: Double,
    val currency: String,
    val method: String,
    val provider: String,
    val cardHolderName: String?,
    val cardToken: String?
)
