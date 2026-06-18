package com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto

data class PaymentCheckoutDto(
    val paymentId: Int,
    val bookingId: Int,
    val provider: String?,
    val checkoutUrl: String?,
    val status: String?
)
