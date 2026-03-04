package com.example.shikiflow

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.presentation.navigation.AppNavigator
import com.example.shikiflow.presentation.viewmodel.AuthState
import com.example.shikiflow.presentation.viewmodel.MainViewModel
import com.example.shikiflow.ui.theme.ShikiFlowTheme
import com.example.shikiflow.utils.ThemeMode.Companion.isDarkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            val themeSettings by mainViewModel.themeSettings.collectAsStateWithLifecycle()
            val authState by mainViewModel.authState.collectAsStateWithLifecycle()

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }

            val systemTheme = isSystemInDarkTheme()
            splashScreen.setKeepOnScreenCondition {
                authState is AuthState.Loading
            }

            ShikiFlowTheme(
                darkTheme = themeSettings.themeMode.isDarkTheme(systemTheme),
                oledTheme = themeSettings.isOledEnabled,
                dynamicColor = themeSettings.isDynamicThemeEnabled,
                paletteStyle = themeSettings.paletteStyle
            ) {
                if(authState != AuthState.Loading) {
                    AppNavigator(
                        authState = authState,
                        onAuthorize = { authType ->
                            mainViewModel.getAuthorizationUrl(authType)
                        },
                        onFinishActivity = { this.moveTaskToBack(true) }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uri = intent.data
        uri?.let {
            mainViewModel.handleAuthCode(it)
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
