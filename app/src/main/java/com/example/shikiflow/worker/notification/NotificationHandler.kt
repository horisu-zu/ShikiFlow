package com.example.shikiflow.worker.notification

sealed interface NotificationHandler {
    suspend fun checkAndNotify()
}