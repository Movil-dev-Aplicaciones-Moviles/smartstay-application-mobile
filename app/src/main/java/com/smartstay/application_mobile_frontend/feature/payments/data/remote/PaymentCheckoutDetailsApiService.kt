package com.smartstay.application_mobile_frontend.feature.payments.data.remote

import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.CheckoutHotelDto
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.CheckoutRoomDto
import retrofit2.http.GET
import retrofit2.http.Path

interface PaymentCheckoutDetailsApiService {
    @GET("hotels/{hotelId}")
    suspend fun getHotelById(@Path("hotelId") hotelId: Int): CheckoutHotelDto

    @GET("rooms/{roomId}")
    suspend fun getRoomById(@Path("roomId") roomId: Int): CheckoutRoomDto
}
