package com.smartstay.application_mobile_frontend.feature.profile.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.domain.model.profiles.Profile
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.User
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import com.smartstay.application_mobile_frontend.feature.profile.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileListUiState {
    data object Loading : ProfileListUiState()
    data class Success(
        val profiles: List<Profile>,
        val users: List<User>
    ) : ProfileListUiState()
    data class Error(val message: String) : ProfileListUiState()
}

@HiltViewModel
class ProfileListViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val iamRepository: IamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileListUiState>(ProfileListUiState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = ProfileListUiState.Loading
        viewModelScope.launch {
            try {

                val profilesDeferred = async { profileRepository.getProfiles() }
                val usersDeferred = async { iamRepository.getUsers() }

                val profilesList = profilesDeferred.await()
                val usersList = usersDeferred.await()

                _uiState.value = ProfileListUiState.Success(
                    profiles = profilesList,
                    users = usersList
                )
            } catch (e: Exception) {
                _uiState.value = ProfileListUiState.Error(e.message ?: "Error al cargar los datos")
            }
        }
    }
}