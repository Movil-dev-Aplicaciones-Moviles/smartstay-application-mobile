package com.smartstay.application_mobile_frontend.feature.payments.domain.model

data class PaymentCheckout(
    val paymentId: Int,
    val bookingId: Int,
    val provider: PaymentProvider,
    val checkoutUrl: String,
    val status: PaymentStatus
)
