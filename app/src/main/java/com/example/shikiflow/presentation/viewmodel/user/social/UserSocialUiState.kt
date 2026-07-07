package com.example.shikiflow.presentation.viewmodel.user.social

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.UserSocial
import com.example.shikiflow.presentation.PagedUiState

data class UserSocialUiState(
    val userId: Int? = null,
    val currentCategory: SocialCategory? = null,
    val categories: Map<SocialCategory, SocialCategoryUiState> = SocialCategory.entries
        .associateWith { SocialCategoryUiState() }
)

data class SocialCategoryUiState(
    val items: SnapshotStateList<UserSocial> = mutableStateListOf(),

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
