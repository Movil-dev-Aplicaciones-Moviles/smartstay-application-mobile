package com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MercadoPagoPreferenceRequestDto(
    val items: List<MercadoPagoPreferenceItemDto>,
    @SerializedName("external_reference") val externalReference: String,
    @SerializedName("back_urls") val backUrls: MercadoPagoBackUrlsDto,
    @SerializedName("auto_return") val autoReturn: String = "approved"
)

data class MercadoPagoPreferenceItemDto(
    val title: String,
    val quantity: Int,
    @SerializedName("currency_id") val currencyId: String,
    @SerializedName("unit_price") val unitPrice: Double
)

data class MercadoPagoBackUrlsDto(
    val success: String,
    val failure: String,
    val pending: String
)

data class MercadoPagoPreferenceResponseDto(
    val id: String?,
    @SerializedName("init_point") val initPoint: String?,
    @SerializedName("sandbox_init_point") val sandboxInitPoint: String?
)
