package com.smartstay.application_mobile_frontend.feature.profile.data.repository
import com.smartstay.application_mobile_frontend.feature.profile.data.remote.ProfileApiService
import com.smartstay.application_mobile_frontend.feature.profile.domain.repository.ProfileRepository
import com.smartstay.application_mobile_frontend.domain.model.profiles.Profile
import com.smartstay.application_mobile_frontend.feature.profile.data.dto.CreateProfileRequest
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApiService
) : ProfileRepository {
    override suspend fun getProfiles(): List<Profile> {
        return api.getProfiles().map { it.toDomain() }
    }
    override suspend fun createProfile(request: CreateProfileRequest): Profile {
        return api.createProfile(request).toDomain()
    }
}