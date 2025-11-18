package com.example.shikiflow.presentation.auth

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shikiflow.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
import com.example.shikiflow.utils.WebIntent

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    AuthMain(
        onStartAuth = {
            scope.launch {
                val authUrl = authViewModel.getAuthorizationUrl()
                Log.d("AuthScreen", "Launching Custom Tab with URL: $authUrl")
                WebIntent.openUrlCustomTab(context, authUrl)
            }
        }
    )
}