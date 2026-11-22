package com.fraudx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fraudx.app.data.LocalFraudStorage
import com.fraudx.app.data.MockFraudXRepository
import com.fraudx.app.theme.VerdantShieldTheme
import com.fraudx.app.ui.navigation.FraudXNavHost
import com.fraudx.app.viewmodel.FraudXViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Connect FraudX to local JSON storage
        val fraudXViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {

                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>
                ): T {

                    val storage = LocalFraudStorage(applicationContext)

                    val repository = MockFraudXRepository(storage)

                    return FraudXViewModel(repository) as T
                }
            }
        )[FraudXViewModel::class.java]

        setContent {
            VerdantShieldTheme {
                FraudXNavHost(
                    viewModel = fraudXViewModel
                )
            }
        }
    }
}