package com.example.shikiflow.domain.repository

import android.net.Uri
import com.example.shikiflow.domain.model.media.UploadedMedia
import com.example.shikiflow.utils.result.DataResult

interface MediaUploaderRepository {
    suspend fun upload(
        uri: Uri,
        mime: String,
        onProgress: (progress: Float) -> Unit
    ): DataResult<UploadedMedia>
}