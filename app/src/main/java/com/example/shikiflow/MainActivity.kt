package com.example.shikiflow

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
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

        enableEdgeToEdge()

        setContent {
            val (darkTheme, oledTheme) = observeTheme(appSettingsManager)

            ShikiFlowTheme(
                darkTheme = darkTheme,
                oledTheme = oledTheme
            ) {
                AppNavigator(appSettingsManager)
            }
        }

        //window.fitSystemWindowsWithAdjustResize()
    }

    @Composable
    private fun observeTheme(appSettingsManager: AppSettingsManager): Pair<Boolean, Boolean> {
        val theme = appSettingsManager.themeFlow.collectAsState(initial = ThemeMode.SYSTEM)
        val isOledEnabled = appSettingsManager.oledFlow.collectAsState(initial = false)

        val systemTheme =
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                else -> false
            }

        Log.d("MainActivity", "Theme: ${theme.value}, OLED: ${isOledEnabled.value}")
        val isDarkTheme = when (theme.value) {
            ThemeMode.SYSTEM -> systemTheme
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }

        return Pair(isDarkTheme, isOledEnabled.value && isDarkTheme)
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

/*fun Window.fitSystemWindowsWithAdjustResize() {
    setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )

    WindowCompat.setDecorFitsSystemWindows(this, true)

    ViewCompat.setOnApplyWindowInsetsListener(decorView) { view, insets ->
        WindowInsetsCompat
            .Builder()
            .setInsets(
                WindowInsetsCompat.Type.systemBars(),
                Insets.of(0, 0, 0, 0)
            )
            .build()
            .apply { ViewCompat.onApplyWindowInsets(view, this) }
    }
}*/
