package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.common.FileSize

interface CacheRepository {
    suspend fun getCacheSize(): FileSize
    suspend fun clearCache(): Boolean
}