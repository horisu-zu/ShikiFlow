package com.example.shikiflow

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.shikiflow.presentation.navigation.AppNavigator
import com.example.shikiflow.presentation.viewmodel.AuthViewModel
import com.example.shikiflow.presentation.viewmodel.ThemeViewModel
import com.example.shikiflow.ui.theme.ShikiFlowTheme
import com.example.shikiflow.utils.ThemeMode.Companion.isDarkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            val themeSettings by themeViewModel.themeSettings.collectAsState()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            val systemTheme = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> true
                Configuration.UI_MODE_NIGHT_NO -> false
                else -> false
            }

            ShikiFlowTheme(
                darkTheme = themeSettings.themeMode.isDarkTheme(systemTheme),
                oledTheme = themeSettings.isOledEnabled,
                dynamicColor = themeSettings.isDynamicThemeEnabled,
                paletteStyle = themeSettings.paletteStyle
            ) {
                AppNavigator(
                    onFinishActivity = { this.moveTaskToBack(true) },
                    onSplashNavigate = {
                        splashScreen.setKeepOnScreenCondition { it }
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uri = intent.data
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
