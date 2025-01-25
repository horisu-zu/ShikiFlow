package com.example.shikiflow

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.shikiflow.presentation.navigation.AppNavigator
import com.example.shikiflow.presentation.viewmodel.AuthViewModel
import com.example.shikiflow.ui.theme.ShikiFlowTheme
import com.example.shikiflow.utils.AppSettingsManager
import com.example.shikiflow.utils.ThemeMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var appSettingsManager: AppSettingsManager
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appSettingsManager = AppSettingsManager(applicationContext)

        setContent {
            val darkTheme = observeTheme(appSettingsManager)

            ShikiFlowTheme(darkTheme = darkTheme) {
                AppNavigator()
            }
        }
    }

    @Composable
    private fun observeTheme(appSettingsManager: AppSettingsManager): Boolean {
        val theme = appSettingsManager.themeFlow.collectAsState(initial = ThemeMode.SYSTEM)
        val systemTheme =
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                else -> false
            }

        Log.d("MainActivity", "Theme: ${theme.value}")
        return when (theme.value) {
            ThemeMode.SYSTEM -> systemTheme
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("NewIntent", "onNewIntent called with intent: $intent")
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val uri = intent?.data
        uri?.let {
            val authCode = uri.getQueryParameter("code")
            authCode?.let {
                Log.d("OAuth", "Authorization code: $it")
                authViewModel.handleAuthCode(it)
            }
        }
    }
}