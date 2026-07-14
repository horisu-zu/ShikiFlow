package com.example.shikiflow.presentation.viewmodel.comment

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.presentation.PagedUiState

data class CommentsUiState(
    val topicId: Int? = null,
    val authType: AuthType? = null,
    val thread: Thread? = null,
    val comments: SnapshotStateList<Comment> = mutableStateListOf(),
    val navState: List<Int> = emptyList(),

    override val page: Int = 1,
    override val hasNextPage: Boolean = true,
    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
): PagedUiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
    override fun setPage(value: Int) = copy(page = value)
    override fun setHasNextPage(value: Boolean) = copy(hasNextPage = value)
}