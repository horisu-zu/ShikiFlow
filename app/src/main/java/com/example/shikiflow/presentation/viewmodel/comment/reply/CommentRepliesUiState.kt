package com.example.shikiflow.presentation.viewmodel.comment.reply

import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.CommentType

data class CommentRepliesUiState(
    val commentId: Int? = null,
    val repliesMap: Map<Int, RepliesUiState> = emptyMap()
)

data class RepliesUiState(
    val commentsMap: Map<CommentType, List<Comment>> = emptyMap(),

    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)