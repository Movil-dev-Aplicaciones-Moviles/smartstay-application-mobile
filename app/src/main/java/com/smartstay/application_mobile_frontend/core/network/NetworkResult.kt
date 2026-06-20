package com.smartstay.application_mobile_frontend.core.network

import retrofit2.HttpException
import java.io.IOException

/**
 * Clase sellada genérica que encapsula el resultado de una operación de red.
 *
 * Estandariza los tres estados posibles en los que puede terminar una llamada
 * a la API, reemplazando los bloques try-catch dispersos en los ViewModels
 * por un mapeo centralizado de excepciones a mensajes amigables.
 *
 * Uso típico:
 * ```
 * val result = safeApiCall { myApiService.getData() }
 * when (result) {
 *     is NetworkResult.Success -> // usar result.data
 *     is NetworkResult.Error   -> // mostrar result.message
 *     is NetworkResult.Loading -> // mostrar indicador de carga
 * }
 * ```
 *
 * @param T El tipo de dato esperado en caso de éxito.
 */
sealed class NetworkResult<out T> {

    /**
     * La operación se completó exitosamente.
     *
     * @property data El dato devuelto por la API.
     */
    data class Success<T>(val data: T) : NetworkResult<T>()

    /**
     * La operación falló con un mensaje descriptivo.
     *
     * @property message Mensaje amigable para mostrar al usuario.
     * @property code Código HTTP del error (nulo si no aplica, ej. error de red).
     */
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()

    /**
     * La operación está en progreso.
     */
    data object Loading : NetworkResult<Nothing>()
}

/**
 * Ejecuta una llamada a la API y envuelve el resultado en un [NetworkResult].
 *
 * Centraliza el manejo de excepciones comunes de Retrofit/OkHttp:
 * - [HttpException]: errores HTTP 4xx/5xx del servidor.
 * - [IOException]: errores de conectividad o timeout.
 * - Cualquier otra [Exception]: fallos imprevistos.
 *
 * @param apiCall Suspend lambda que ejecuta la llamada Retrofit.
 * @return [NetworkResult.Success] con el dato, o [NetworkResult.Error] con un mensaje amigable.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        val result = apiCall()
        NetworkResult.Success(result)
    } catch (e: HttpException) {
        NetworkResult.Error(
            message = "Error del servidor: ${e.code()}",
            code = e.code()
        )
    } catch (e: IOException) {
        NetworkResult.Error(
            message = "Error de conexión. Verifica tu internet.",
            code = null
        )
    } catch (e: Exception) {
        NetworkResult.Error(
            message = e.message ?: "Error desconocido",
            code = null
        )
    }
}
