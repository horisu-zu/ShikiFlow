package com.example.shikiflow.domain.repository

interface CacheRepository {
    suspend fun getCacheSize(): String
    suspend fun clearCache(): Boolean
}