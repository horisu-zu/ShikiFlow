package com.example.shikiflow.presentation.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shikiflow.presentation.viewmodel.AuthViewModel
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.launch
import androidx.core.net.toUri

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val customTabsIntent = CustomTabsIntent.Builder().build()

    AuthMain(
        onStartAuth = {
            scope.launch {
                val authUrl = authViewModel.getAuthorizationUrl()
                Log.d("AuthScreen", "Launching Custom Tab with URL: $authUrl")
                customTabsIntent.launchUrl(context, authUrl.toUri())
            }
        }
    )
}