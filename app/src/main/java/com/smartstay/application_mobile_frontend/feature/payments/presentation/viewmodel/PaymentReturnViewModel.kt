package com.smartstay.application_mobile_frontend.feature.payments.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.PaymentApiService
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.CreatePaymentRequestDto
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentReturnUiState(
    val isRegisteringPayment: Boolean = false,
    val backendMessage: String? = null,
    val backendError: String? = null
)

@HiltViewModel
class PaymentReturnViewModel @Inject constructor(
    private val paymentApiService: PaymentApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentReturnUiState())
    val uiState: StateFlow<PaymentReturnUiState> = _uiState.asStateFlow()

    fun registerSuccessfulPayment(status: String, bookingId: Int?, amount: Double?) {
        if (!status.equals("success", ignoreCase = true)) return

        if (bookingId == null || amount == null || amount <= 0.0) {
            _uiState.update {
                it.copy(
                    isRegisteringPayment = false,
                    backendMessage = null,
                    backendError = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRegisteringPayment = true,
                    backendMessage = null,
                    backendError = null
                )
            }

            runCatching {
                paymentApiService.createPayment(
                    CreatePaymentRequestDto(
                        bookingId = bookingId,
                        amount = amount,
                        paymentMethod = "MERCADO_PAGO",
                        cardNumber = "4009175323280616",
                        cardHolderName = "APRO",
                        expirationDate = "11/30",
                        cvv = "123"
                    )
                )
            }.onSuccess {
                _uiState.update {
                    it.copy(
                        isRegisteringPayment = false,
                        backendMessage = "Pago registrado en backend. La reserva fue confirmada.",
                        backendError = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isRegisteringPayment = false,
                        backendMessage = null,
                        backendError = "Mercado Pago aprobo el pago, pero no se pudo registrar en backend: ${error.message}"
                    )
                }
            }
        }
    }
}
