package com.example.shikiflow.data.repository

import android.content.Context
import com.example.shikiflow.R
import com.example.shikiflow.domain.repository.CacheRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): CacheRepository {

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
            size < 1024 -> context.getString(R.string.cache_size_bytes, size)
            size < 1024 * 1024 -> context.getString(R.string.cache_size_kbytes).format(size.toDouble() / 1024)
            size < 1024 * 1024 * 1024 -> context.getString(R.string.cache_size_mbytes).format(size.toDouble() / (1024 * 1024))
            else -> context.getString(R.string.cache_size_gbytes).format(size.toDouble() / (1024 * 1024 * 1024))
        }
    }
}