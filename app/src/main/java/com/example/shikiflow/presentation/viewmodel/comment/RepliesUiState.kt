package com.example.shikiflow.presentation.viewmodel.comment

import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.CommentType

data class RepliesUiState(
    val commentsMap: Map<CommentType, List<Comment>> = emptyMap(),

    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null
)
