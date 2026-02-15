package com.example.shikiflow.data.repository

import android.content.Context
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.FileSize
import com.example.shikiflow.domain.repository.CacheRepository
import com.example.shikiflow.utils.Converter.formatFileSize
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class CacheRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
): CacheRepository {

    override suspend fun getCacheSize(): FileSize {
        return withContext(Dispatchers.IO) {
            val imageCacheDir = File(context.cacheDir, "image_cache")
            val cacheSize = calculateDirSize(imageCacheDir)
            formatFileSize(cacheSize.toDouble())
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
}