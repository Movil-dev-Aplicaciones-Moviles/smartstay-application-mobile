package com.smartstay.application_mobile_frontend.feature.payments.data.remote

import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.CreatePaymentRequestDto
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.PaymentCheckoutDto
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.PaymentDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApiService {
    @POST("payments")
    suspend fun createPayment(@Body request: CreatePaymentRequestDto): PaymentDto

    @POST("payments/mercado-pago/checkout")
    suspend fun createMercadoPagoCheckout(@Body request: CreatePaymentRequestDto): PaymentCheckoutDto

    @GET("payments/{id}")
    suspend fun getPaymentById(@Path("id") id: Int): PaymentDto

    @GET("payments/booking/{bookingId}")
    suspend fun getPaymentsByBookingId(@Path("bookingId") bookingId: Int): List<PaymentDto>
}
