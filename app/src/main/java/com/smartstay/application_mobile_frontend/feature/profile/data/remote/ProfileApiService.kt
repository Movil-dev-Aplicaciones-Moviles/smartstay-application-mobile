package com.smartstay.application_mobile_frontend.feature.profile.data.remote

import com.smartstay.application_mobile_frontend.feature.profile.data.dto.CreateProfileRequest
import com.smartstay.application_mobile_frontend.feature.profile.data.dto.ProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ProfileApiService {
    @GET("profiles")
    suspend fun getProfiles(): List<ProfileDto>

    @POST("profiles")
    suspend fun createProfile(@Body request: CreateProfileRequest): ProfileDto
}