package com.example.shikiflow.presentation.viewmodel.comment.editor

import com.example.shikiflow.domain.model.comment.MarkdownFormat
import com.example.shikiflow.domain.model.media.UploadedMedia

sealed interface UploadMediaState {
    data object Idle : UploadMediaState
    data class Uploading(val progress: Float) : UploadMediaState
    data class Success(val media: UploadedMedia, val format: MarkdownFormat) : UploadMediaState
    data class Error(val message: String) : UploadMediaState
}