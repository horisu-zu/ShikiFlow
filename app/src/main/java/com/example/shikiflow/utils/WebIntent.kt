package com.example.shikiflow.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

object WebIntent {
    fun openUrlCustomTab(context: Context, url: String) {
        try {
            val uri = url.toUri()
            val customTabsIntent = CustomTabsIntent.Builder()
                .setShowTitle(true)
                .build()

            customTabsIntent.launchUrl(context, uri)
        } catch (e: Exception) {
            Log.d("WebIntent", "Error opening $url: ${e.message}")
        }
    }

    fun openActionView(context: Context, url: String) {
        try {
            val uri = url.toUri()

            Intent(Intent.ACTION_VIEW, uri).apply {
                context.startActivity(this)
            }
        } catch (e: Exception) {
            Log.d("WebIntent", "Error opening $url: ${e.message}")
        }
    }
}