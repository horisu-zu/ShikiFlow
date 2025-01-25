package com.example.shikiflow.presentation.auth

import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.presentation.viewmodel.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun AuthWeb(
    scope: CoroutineScope,
    authViewModel: AuthViewModel = hiltViewModel(),
    onHandleAuth: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        allowFileAccess = false
                        cacheMode = WebSettings.LOAD_NO_CACHE
                        domStorageEnabled = true
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                    }
                    clearCache(true)

                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView?,
                            request: WebResourceRequest?
                        ): Boolean {
                            val url = request?.url?.toString() ?: return false

                            if (url.contains("/oauth/authorize/")) {
                                val code = url.substringAfterLast("/")
                                scope.launch {
                                    authViewModel.handleAuthCode(code)
                                    onHandleAuth()
                                }
                                return true
                            }
                            return false
                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            Log.e("WebView", "Error: ${error?.description}")
                            //view?.reload()
                        }
                    }
                    scope.launch {
                        loadUrl(authViewModel.getAuthorizationUrl())
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
