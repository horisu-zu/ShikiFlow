package com.example.shikiflow.presentation.viewmodel.comment.section

import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.presentation.UiState

data class CommentSectionUiState(
    val topicId: Int? = null,
    val commentsCount: Int? = null,
    val comments: List<Comment> = emptyList(),

    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}