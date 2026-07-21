package com.example.shikiflow.presentation.viewmodel.comment.editor

import android.net.Uri
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.MarkdownFormat

data class CommentEditorUiState(
    val threadId: Int? = null,
    val commentId: Int? = null,
    val commentBody: String? = null,
    val parentCommentId: Int? = null,
    val authType: AuthType? = null,
    val isOfftopic: Boolean = false,

    val format: MarkdownFormat? = null,
    val uri: Uri? = null,
    val uploadMediaState: UploadMediaState = UploadMediaState.Idle,
    val isRefreshingUpload: Boolean = false
)
