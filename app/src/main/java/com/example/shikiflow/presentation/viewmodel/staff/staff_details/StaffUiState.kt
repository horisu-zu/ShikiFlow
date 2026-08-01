package com.example.shikiflow.presentation.viewmodel.staff.staff_details

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.staff.StaffDetails
import com.example.shikiflow.presentation.UiState

data class StaffUiState(
    val staffId: Int? = null,
    val authType: AuthType? = null,
    val staffDetails: StaffDetails? = null,

    override val errorMessage: String? = null,
    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
) : UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}