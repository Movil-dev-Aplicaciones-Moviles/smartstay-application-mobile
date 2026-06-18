package com.smartstay.application_mobile_frontend.feature.payments.data.remote

import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.MercadoPagoPreferenceRequestDto
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.MercadoPagoPreferenceResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface MercadoPagoApiService {
    @POST("checkout/preferences")
    suspend fun createPreference(
        @Header("Authorization") authorization: String,
        @Body request: MercadoPagoPreferenceRequestDto
    ): MercadoPagoPreferenceResponseDto
}
