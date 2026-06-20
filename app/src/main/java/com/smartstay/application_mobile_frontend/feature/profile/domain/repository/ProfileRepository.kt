package com.smartstay.application_mobile_frontend.feature.profile.domain.repository
import com.smartstay.application_mobile_frontend.domain.model.profiles.Profile
import com.smartstay.application_mobile_frontend.feature.profile.data.dto.CreateProfileRequest

interface ProfileRepository {
    suspend fun getProfiles(): List<Profile>
    suspend fun createProfile(request: CreateProfileRequest): Profile
    suspend fun getProfileById(profileId: Int): Profile
}