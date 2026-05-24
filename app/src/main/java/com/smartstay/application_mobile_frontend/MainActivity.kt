package com.smartstay.application_mobile_frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.smartstay.application_mobile_frontend.core.navigation.SmartStayNavGraph
import com.smartstay.application_mobile_frontend.ui.theme.ApplicationmobilefrontendTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApplicationmobilefrontendTheme {
                SmartStayNavGraph()
            }
        }
    }
}