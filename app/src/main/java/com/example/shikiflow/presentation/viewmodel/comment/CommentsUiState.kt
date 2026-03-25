package com.example.shikiflow.presentation.viewmodel.comment

data class CommentsUiState(
    val topicId: Int? = null,
    val commentId: Int? = null,
    val repliesMap: Map<Int, RepliesUiState> = emptyMap()
)
