package com.smartstay.application_mobile_frontend.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase base de la aplicación requerida por Hilt para la
 * generación de componentes y la inyección de dependencias.
 */
@HiltAndroidApp
class SmartStayApplication : Application()