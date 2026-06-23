package com.smartstay.application_mobile_frontend.feature.accommodation.data.remote

import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.HotelDto
import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.RoomDto
import com.smartstay.application_mobile_frontend.feature.accommodation.data.remote.dto.RoomTypeDto
import retrofit2.http.*

interface AccommodationApiService {
    
    // --- Hotels ---
    @GET("hotels")
    suspend fun fetchHotels(): List<HotelDto>

    @GET("hotels/{id}")
    suspend fun getHotelById(@Path("id") hotelId: Int): HotelDto

    @POST("hotels")
    suspend fun createHotel(@Body hotel: HotelDto): HotelDto

    @PUT("hotels/{id}")
    suspend fun updateHotel(@Path("id") hotelId: Int, @Body hotel: HotelDto): HotelDto

    // --- Rooms ---
    @GET("rooms")
    suspend fun getRooms(@Query("hotelId") hotelId: Int? = null): List<RoomDto>

    @GET("rooms/{id}")
    suspend fun getRoomById(@Path("id") roomId: Int): RoomDto

    @POST("rooms")
    suspend fun createRoom(@Body room: RoomDto): RoomDto

    @PUT("rooms/{id}")
    suspend fun updateRoom(@Path("id") roomId: Int, @Body room: RoomDto): RoomDto

    @DELETE("rooms/{id}")
    suspend fun deleteRoom(@Path("id") roomId: Int)

    @GET("rooms/type/{roomTypeId}")
    suspend fun getRoomsByType(@Path("roomTypeId") roomTypeId: Int): List<RoomDto>

    // --- Room Types ---
    @GET("room-types")
    suspend fun getRoomTypes(): List<RoomTypeDto>

    @POST("room-types")
    suspend fun createRoomType(@Body roomType: RoomTypeDto): RoomTypeDto
}
