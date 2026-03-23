package com.example.shikiflow.presentation.viewmodel.user.profile

import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.presentation.UiState

data class ProfileUiState(
    val userId: Int? = null,
    val userStatsCategories: UserStatsCategories = UserStatsCategories(),

    override val errorMessage: String? = null,
    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
) : UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}