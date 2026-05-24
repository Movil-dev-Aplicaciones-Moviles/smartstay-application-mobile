// feature/iam/presentation/viewmodel/IamViewModel.kt
package com.smartstay.application_mobile_frontend.feature.iam.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.iam.domain.model.SignInCommand
import com.smartstay.application_mobile_frontend.feature.iam.domain.repository.IamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IamUiState(
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val errors: List<String> = emptyList()
)

@HiltViewModel
class IamViewModel @Inject constructor(
    private val repository: IamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IamUiState(isSignedIn = repository.isSignedIn()))
    val uiState = _uiState.asStateFlow()

    fun signIn(command: SignInCommand, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errors = emptyList())
            try {
                val user = repository.signIn(command)
                _uiState.value = _uiState.value.copy(isSignedIn = true, isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSignedIn = false,
                    isLoading = false,
                    errors = listOf(e.message ?: "Error al iniciar sesión")
                )
            }
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        repository.signOut()
        _uiState.value = IamUiState(isSignedIn = false)
        onSuccess()
    }
}