package com.example.shikiflow.presentation.viewmodel.comment.editor

import com.example.shikiflow.domain.model.auth.AuthType

data class CommentEditorUiState(
    val threadId: Int? = null,
    val commentId: Int? = null,
    val commentBody: String? = null,
    val parentCommentId: Int? = null,
    val authType: AuthType? = null,
    val isOfftopic: Boolean = false,
    val uploadMediaState: UploadMediaState = UploadMediaState.Idle
)
