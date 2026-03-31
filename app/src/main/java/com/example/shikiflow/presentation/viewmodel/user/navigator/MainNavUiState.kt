package com.example.shikiflow.presentation.viewmodel.user.navigator

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.UiState

data class MainNavUiState(
    val user: User? = null,
    val authType: AuthType? = null,

    override val isLoading: Boolean = true,
    override val errorMessage: String? = null
) : UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}