package com.smartstay.application_mobile_frontend.feature.payments.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.BuildConfig
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.MercadoPagoApiService
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.PaymentCheckoutDetailsApiService
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.MercadoPagoBackUrlsDto
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.MercadoPagoPreferenceItemDto
import com.smartstay.application_mobile_frontend.feature.payments.data.remote.dto.MercadoPagoPreferenceRequestDto
import com.smartstay.application_mobile_frontend.feature.payments.domain.model.PaymentMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PaymentCheckoutUiState(
    val bookingId: Int? = null,
    val hotelId: Int? = null,
    val roomId: Int? = null,
    val hotelName: String = "SmartStay Hotel Lima",
    val hotelLocation: String? = null,
    val roomTypeName: String? = null,
    val roomDescription: String? = null,
    val nights: Int = 2,
    val pricePerNight: Double = 250.00,
    val currency: String = "PEN",
    val selectedMethod: PaymentMethod = PaymentMethod.MERCADO_PAGO,
    val checkoutUrl: String? = null,
    val isLoadingDetails: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val totalAmount: Double
        get() = pricePerNight * nights
}

@HiltViewModel
class PaymentCheckoutViewModel @Inject constructor(
    private val checkoutDetailsApiService: PaymentCheckoutDetailsApiService,
    private val mercadoPagoApiService: MercadoPagoApiService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentCheckoutUiState())
    val uiState: StateFlow<PaymentCheckoutUiState> = _uiState.asStateFlow()

    fun loadCheckoutDetails(
        bookingId: Int?,
        hotelId: Int?,
        roomId: Int?,
        amount: Double?,
        hotelName: String?
    ) {
        _uiState.update {
            it.copy(
                bookingId = bookingId,
                hotelId = hotelId,
                roomId = roomId,
                pricePerNight = amount ?: it.pricePerNight,
                hotelName = hotelName?.ifBlank { null } ?: it.hotelName,
                checkoutUrl = null,
                errorMessage = null
            )
        }

        if (hotelId == null && roomId == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingDetails = true, errorMessage = null) }

            var resolvedHotelId = hotelId
            val roomResult = roomId?.let { id ->
                runCatching { checkoutDetailsApiService.getRoomById(id) }
            }

            roomResult?.onSuccess { room ->
                resolvedHotelId = room.hotelId
                _uiState.update {
                    it.copy(
                        roomId = room.id,
                        hotelId = room.hotelId,
                        roomTypeName = room.roomTypeName,
                        roomDescription = room.description,
                        pricePerNight = room.price
                    )
                }
            }?.onFailure { error ->
                _uiState.update {
                    it.copy(errorMessage = "No se pudo cargar la habitacion: ${error.message}")
                }
            }

            resolvedHotelId?.let { id ->
                runCatching { checkoutDetailsApiService.getHotelById(id) }
                    .onSuccess { hotel ->
                        _uiState.update {
                            it.copy(
                                hotelId = hotel.id,
                                hotelName = hotel.name,
                                hotelLocation = hotel.location,
                                pricePerNight = if (roomResult == null && amount == null) {
                                    hotel.basePrice ?: it.pricePerNight
                                } else {
                                    it.pricePerNight
                                }
                            )
                        }
                    }
                    .onFailure { error ->
                        _uiState.update {
                            it.copy(errorMessage = "No se pudo cargar el hotel: ${error.message}")
                        }
                    }
            }

            _uiState.update { it.copy(isLoadingDetails = false) }
        }
    }

    fun createMercadoPagoCheckout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, checkoutUrl = null, errorMessage = null) }

            val fixedCheckoutUrl = BuildConfig.MERCADO_PAGO_CHECKOUT_URL
            if (fixedCheckoutUrl.isNotBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        checkoutUrl = fixedCheckoutUrl,
                        errorMessage = null
                    )
                }
                return@launch
            }

            val accessToken = BuildConfig.MERCADO_PAGO_ACCESS_TOKEN
            if (accessToken.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Configura MERCADO_PAGO_ACCESS_TOKEN en local.properties para crear el checkout real de prueba."
                    )
                }
                return@launch
            }

            val state = _uiState.value
            val returnQuery = "bookingId=${state.bookingId ?: -1}&amount=${state.totalAmount}"
            val request = MercadoPagoPreferenceRequestDto(
                items = listOf(
                    MercadoPagoPreferenceItemDto(
                        title = buildPreferenceTitle(state),
                        quantity = 1,
                        currencyId = state.currency,
                        unitPrice = state.totalAmount
                    )
                ),
                externalReference = buildExternalReference(state),
                backUrls = MercadoPagoBackUrlsDto(
                    success = "smartstay://payment/success?$returnQuery",
                    failure = "smartstay://payment/failure?$returnQuery",
                    pending = "smartstay://payment/pending?$returnQuery"
                )
            )

            runCatching {
                mercadoPagoApiService.createPreference(
                    authorization = "Bearer $accessToken",
                    request = request
                )
            }.onSuccess { preference ->
                val checkoutUrl = preference.sandboxInitPoint ?: preference.initPoint
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        checkoutUrl = checkoutUrl,
                        errorMessage = if (checkoutUrl.isNullOrBlank()) {
                            "Mercado Pago no devolvio una URL de checkout."
                        } else {
                            null
                        }
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudo crear la preferencia de Mercado Pago: ${error.message}"
                    )
                }
            }
        }
    }

    fun resetCheckout() {
        _uiState.update {
            it.copy(
                checkoutUrl = null,
                errorMessage = null
            )
        }
    }

    fun increaseNights() {
        _uiState.update {
            it.copy(
                nights = (it.nights + 1).coerceAtMost(30),
                checkoutUrl = null,
                errorMessage = null
            )
        }
    }

    fun decreaseNights() {
        _uiState.update {
            it.copy(
                nights = (it.nights - 1).coerceAtLeast(1),
                checkoutUrl = null,
                errorMessage = null
            )
        }
    }
}

private fun buildPreferenceTitle(state: PaymentCheckoutUiState): String {
    return listOfNotNull(
        state.hotelName,
        state.roomTypeName?.let { "Habitacion $it" },
        state.roomId?.let { "Room #$it" }
    ).joinToString(" - ")
}

private fun buildExternalReference(state: PaymentCheckoutUiState): String {
    return listOfNotNull(
        state.bookingId?.let { "booking-$it" },
        state.hotelId?.let { "hotel-$it" },
        state.roomId?.let { "room-$it" }
    ).ifEmpty {
        listOf("smartstay-demo-${System.currentTimeMillis()}")
    }.joinToString("_")
}
