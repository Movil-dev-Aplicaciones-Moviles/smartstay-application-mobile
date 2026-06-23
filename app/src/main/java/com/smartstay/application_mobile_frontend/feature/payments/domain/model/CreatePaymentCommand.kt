package com.smartstay.application_mobile_frontend.feature.payments.domain.model

data class CreatePaymentCommand(
    val bookingId: Int,
    val amount: Double,
    val currency: String = "PEN",
    val method: PaymentMethod,
    val provider: PaymentProvider = PaymentProvider.MOCK,
    val cardHolderName: String? = null,
    val cardToken: String? = null
)
