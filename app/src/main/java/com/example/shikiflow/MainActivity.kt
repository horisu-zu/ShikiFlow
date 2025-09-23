package com.example.shikiflow

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.shikiflow.presentation.navigation.AppNavigator
import com.example.shikiflow.presentation.viewmodel.AuthViewModel
import com.example.shikiflow.presentation.viewmodel.ThemeViewModel
import com.example.shikiflow.ui.theme.ShikiFlowTheme
import com.example.shikiflow.utils.ThemeMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val (darkTheme, oledTheme) = observeTheme()

            ShikiFlowTheme(
                darkTheme = darkTheme,
                oledTheme = oledTheme
            ) {
                AppNavigator(onFinishActivity = { this.finish() })
            }
        }

        //window.fitSystemWindowsWithAdjustResize()
    }

    @Composable
    private fun observeTheme(): Pair<Boolean, Boolean> {
        val settings by themeViewModel.themeSettings.collectAsState()

        val systemTheme =
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                else -> false
            }

        Log.d("MainActivity", "Theme: ${settings.themeMode}, OLED: ${settings.isOledEnabled}")
        val isDarkTheme = when (settings.themeMode) {
            ThemeMode.SYSTEM -> systemTheme
            ThemeMode.LIGHT -> false
            ThemeMode.DARK -> true
        }

        return Pair(isDarkTheme, settings.isOledEnabled && isDarkTheme)
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
