package com.example.shikiflow.worker.notification

interface NotificationHandler {
    suspend fun checkAndNotify()
}