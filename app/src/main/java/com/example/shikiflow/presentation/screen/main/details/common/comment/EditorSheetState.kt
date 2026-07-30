package com.example.shikiflow.presentation.screen.main.details.common.comment

data class EditorSheetState(
    val id: Int,
    val entryId: Int? = null,
    val body: String? = null,
    val parentCommentId: Int? = null
)
