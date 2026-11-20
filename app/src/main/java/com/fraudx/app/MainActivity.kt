package com.fraudx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fraudx.app.theme.VerdantShieldTheme
import com.fraudx.app.ui.navigation.FraudXNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VerdantShieldTheme {
                FraudXNavHost()
            }
        }
    }
}
