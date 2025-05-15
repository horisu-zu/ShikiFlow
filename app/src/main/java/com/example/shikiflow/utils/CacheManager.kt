package com.example.shikiflow.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

interface CacheManager {
    suspend fun getCacheSize(): String
    suspend fun clearCache(): Boolean
}

@Singleton
class CacheManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): CacheManager {

    override suspend fun getCacheSize(): String {
        return withContext(Dispatchers.IO) {
            val cacheSize = calculateDirSize(context.cacheDir)
            formatSize(cacheSize)
        }
    }

    override suspend fun clearCache(): Boolean {
        return withContext(Dispatchers.IO) {
            deleteDir(context.cacheDir)
        }
    }

    private fun calculateDirSize(dir: File): Long {
        if (!dir.exists()) return 0L

        var size = 0L

        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) {
                calculateDirSize(file)
            } else {
                file.length()
            }
        }

        return size
    }

    private fun deleteDir(dir: File): Boolean {
        if (dir.isDirectory) {
            dir.listFiles()?.forEach { file ->
                deleteDir(file)
            }
        }
        return dir.delete()
    }

    private fun formatSize(size: Long): String {
        return when {
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> "%.2f KB".format(size.toDouble() / 1024)
            size < 1024 * 1024 * 1024 -> "%.2f MB".format(size.toDouble() / (1024 * 1024))
            else -> "%.2f GB".format(size.toDouble() / (1024 * 1024 * 1024))
        }
    }
}