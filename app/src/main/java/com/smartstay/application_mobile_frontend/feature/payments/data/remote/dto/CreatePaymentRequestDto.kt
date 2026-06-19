package com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto

data class CreatePaymentRequestDto(
    val bookingId: Int,
    val amount: Double,
    val paymentMethod: String,
    val cardNumber: String,
    val cardHolderName: String,
    val expirationDate: String,
    val cvv: String
)
