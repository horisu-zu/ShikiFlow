package com.example.shikiflow.presentation.viewmodel.media.review

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.review.ReviewRating
import com.example.shikiflow.domain.repository.ReviewRepository
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository
): UiStateViewModel<ReviewUiState>() {

    override val initialState: ReviewUiState = ReviewUiState()

    fun setId(reviewId: Int) {
        mutableUiState.update { state ->
            state.copy(reviewId = reviewId)
        }
    }

    fun toggleRating(
        reviewId: Int,
        rating: ReviewRating,
        isUserRating: Boolean
    ) {
        viewModelScope.launch {
            reviewRepository.toggleReviewRating(
                reviewId = reviewId,
                rating = if (isUserRating) ReviewRating.NO_VOTE else rating
            ).let { result ->
                if(result is DataResult.Success) {
                    val reviewRate = result.data

                    mutableUiState.update { state ->
                        state.copy(
                            review = state.review?.copy(
                                userRating = reviewRate.userRating,
                                likesCount = reviewRate.rating,
                                ratingAmount = reviewRate.ratingAmount
                            )
                        )
                    }
                }
            }
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(isRefreshing = true)
        }
    }

    init {
        mutableUiState
            .filter { state ->
                state.reviewId != null
            }
            .distinctUntilChanged { old, new ->
                old.reviewId == new.reviewId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                reviewRepository.getReview(state.reviewId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when(result) {
                        DataResult.Loading -> {
                            state.copy(
                                isLoading = true,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }
                        is DataResult.Success -> {
                            state.copy(
                                review = result.data,
                                isLoading = false
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                errorMessage = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}