package com.example.shikiflow.presentation.viewmodel.user.activity.reply

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.shikiflow.domain.model.user.activity.ActivityReply
import com.example.shikiflow.domain.model.user.activity.UserActivity
import com.example.shikiflow.presentation.PagedUiState

data class ActivityRepliesUiState(
    val activityId: Int? = null,
    val currentUserId: Int? = null,

    val activity: UserActivity? = null,
    val isLoadingActivity: Boolean = true,

    val replies: SnapshotStateList<ActivityReply> = mutableStateListOf(),
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
