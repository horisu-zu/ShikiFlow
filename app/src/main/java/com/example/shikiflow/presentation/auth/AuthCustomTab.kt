package com.example.shikiflow.presentation.auth

/*
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AuthCustomTab(
    scope: CoroutineScope,
    authViewModel: AuthViewModel = hiltViewModel(),
    onHandleAuth: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val url = result.data?.dataString
        if (url != null && url.contains("oauth/authorize")) {
            val code = url.substringAfterLast("/")
            Log.d("AuthCustomTab", "Authorization Code: $code")
            scope.launch {
                authViewModel.handleAuthCode(code)
                onHandleAuth()
            }
        } else {
            Log.e("AuthCustomTab", "Authorization failed or URL is null")
        }
    }

    LaunchedEffect(Unit) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setUrlBarHidingEnabled(false)
            .build()

        val authUrl = authViewModel.getAuthorizationUrl()

        launcher.launch(
            customTabsIntent.intent.apply {
                data = Uri.parse(authUrl)
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        )
    }
}*/
