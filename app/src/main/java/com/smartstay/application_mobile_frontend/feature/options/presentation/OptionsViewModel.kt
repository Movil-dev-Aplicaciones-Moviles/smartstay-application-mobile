package com.smartstay.application_mobile_frontend.feature.options.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smartstay.application_mobile_frontend.feature.options.domain.model.Amenity
import com.smartstay.application_mobile_frontend.feature.options.domain.model.HotelCategory
import com.smartstay.application_mobile_frontend.feature.options.domain.repository.OptionsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OptionsViewModel @Inject constructor(
    private val repository: OptionsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OptionsUiState())
    val uiState: StateFlow<OptionsUiState> = _uiState.asStateFlow()

    init {
        loadOptions()
    }

    fun loadOptions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Simula un pequeño retraso de red de 1 segundo para ver el cargando (opcional)
            delay(1000)

            // ⚠️ MOCK: Datos de prueba para verificar el diseño visual en el emulador
            val mockCategories = listOf(
                HotelCategory(id = 1, name = "Economico", description = "Habitaciones cómodas a precios accesibles, ideales para mochileros."),
                HotelCategory(id = 2, name = "Boutique", description = "Diseño exclusivo y atención personalizada en ubicaciones céntricas."),
                HotelCategory(id = 3, name = "Resort & Spa", description = "Instalaciones de lujo con todo incluido para el máximo confort.")
            )

            val mockAmenities = listOf(
                Amenity(id = 1, name = "Wi-Fi Premium", description = "Conexión simétrica de alta velocidad apta para videollamadas."),
                Amenity(id = 2, name = "Piscina Climatizada", description = "Acceso libre para huéspedes de 6:00 AM a 11:00 PM."),
                Amenity(id = 3, name = "Estacionamiento Gratuito", description = "Vigilancia privada las 24 horas del día."),
                Amenity(id = 4, name = "Desayuno Americano", description = "Servido en el restaurante principal de 7:00 AM a 10:30 AM.")
            )

            // Actualizamos el estado directamente con los datos falsos saltando el repositorio
            _uiState.update {
                it.copy(
                    isLoading = false,
                    amenities = mockAmenities,
                    categories = mockCategories
                )
            }

            /* COMENTADO TEMPORALMENTE: Lógica real con el repositorio de Smart Stay
            val amenitiesResult = repository.getAmenities()
            val categoriesResult = repository.getHotelCategories()

            if (amenitiesResult.isSuccess && categoriesResult.isSuccess) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        amenities = amenitiesResult.getOrNull() ?: emptyList(),
                        categories = categoriesResult.getOrNull() ?: emptyList()
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al cargar las opciones de alojamiento"
                    )
                }
            }
            */
        }
    }
}