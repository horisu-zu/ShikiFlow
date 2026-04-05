package com.example.shikiflow.presentation.viewmodel.media.review

import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.presentation.UiState

data class ReviewUiState(
    val reviewId: Int? = null,
    val review: Review? = null,

    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
) : UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
