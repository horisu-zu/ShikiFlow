package com.example.shikiflow.presentation.screen.main.details.common.comment

data class EditorSheetState(
    val threadId: Int,
    val commentId: Int? = null,
    val commentBody: String? = null,
    val parentCommentId: Int? = null
)
