package com.example.shikiflow.data.uploader

import android.net.Uri
import com.example.shikiflow.domain.model.media.UploadedMedia
import com.example.shikiflow.utils.result.DataResult

interface MediaUploader {
    suspend fun upload(
        uri: Uri,
        mime: String,
        onProgress: (progress: Float) -> Unit
    ): DataResult<UploadedMedia>
}