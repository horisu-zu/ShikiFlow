package com.example.shikiflow.presentation.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shikiflow.presentation.viewmodel.AuthViewModel
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.example.shikiflow.presentation.viewmodel.AuthState

@Composable
fun AuthScreen(
    navController: NavController,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val customTabsIntent = CustomTabsIntent.Builder().build()

    val authState by authViewModel.authState.collectAsState()

    AuthMain(
        onStartAuth = {
            scope.launch {
                val authUrl = authViewModel.getAuthorizationUrl()
                Log.d("AuthScreen", "Launching Custom Tab with URL: $authUrl")
                customTabsIntent.launchUrl(context, authUrl.toUri())
            }
        }
    )

    LaunchedEffect(authState) {
        when(authState) {
            AuthState.Success -> {
                Log.d("AuthScreen", "Navigating to 'main'")
                navController.navigate("main") {
                    popUpTo("auth") { inclusive = true }
                }
            }
            else -> {}
        }
    }
}