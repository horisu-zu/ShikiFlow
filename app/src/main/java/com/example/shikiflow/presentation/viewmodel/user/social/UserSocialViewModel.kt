package com.example.shikiflow.presentation.viewmodel.user.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.user.social.SocialCategory
import com.example.shikiflow.domain.model.user.social.ThreadComment
import com.example.shikiflow.domain.repository.ActivityRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
class UserSocialViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val activityRepository: ActivityRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(UserSocialUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState
            .filter { state ->
                state.categories[state.currentCategory]?.hasNextPage == true &&
                state.userId != null && state.currentCategory != null
            }
            .distinctUntilChanged { old, new ->
                old.currentCategory == new.currentCategory &&
                old.categories[old.currentCategory]?.page == new.categories[new.currentCategory]?.page &&
                new.categories[new.currentCategory]?.isRefreshing == false
            }
            .flatMapLatest { state ->
                userRepository.getUserSocial(
                    userId = state.userId!!,
                    socialCategory = state.currentCategory!!,
                    page = state.categories[state.currentCategory]?.page ?: 1
                )
            }
            .onEach { result ->
                _uiState.update { state ->
                    val category = state.currentCategory!!

                    when (result) {
                        is PagedResult.Loading -> {
                            state.updateCategory(category) { category ->
                                category.copy(
                                    isLoading = true,
                                    isRefreshing = false,
                                    errorMessage = null
                                )
                            }
                        }
                        is PagedResult.Error -> {
                            state.updateCategory(category) { category ->
                                category.copy(
                                    isLoading = false,
                                    errorMessage = result.message,
                                    hasNextPage = false
                                )
                            }
                        }
                        is PagedResult.Success -> {
                            val categoryState = state.categories.getValue(category)
                            categoryState.items.addAll(result.list)

                            state.updateCategory(category) { category ->
                                category.copy(
                                    isLoading = false,
                                    hasNextPage = result.hasNextPage
                                )
                            }
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun setUserId(userId: Int) {
        _uiState.update { state ->
            state.copy(userId = userId)
        }
    }

    fun setCategory(socialCategory: SocialCategory) {
        _uiState.update { state ->
            state.copy(currentCategory = socialCategory)
        }
    }

    fun refresh(socialCategory: SocialCategory) {
        _uiState.update { state ->
            state.updateCategory(socialCategory) { category ->
                category.items.clear()

                category.copy(
                    page = 1,
                    isRefreshing = true,
                    hasNextPage = true
                )
            }
        }
    }

    fun retry(socialCategory: SocialCategory) {
        _uiState.update { state ->
            state.updateCategory(socialCategory) { category ->
                category.copy(
                    isRefreshing = true,
                    hasNextPage = true
                )
            }
        }
    }

    fun onLoadMore(socialCategory: SocialCategory) {
        _uiState.update { state ->
            state.updateCategory(socialCategory) { category ->
                category.copy(
                    page = category.page + 1
                )
            }
        }
    }

    fun toggleCommentLike(commentId: Int) {
        viewModelScope.launch {
            activityRepository.toggleLike(commentId, LikeableType.COMMENT).let { result ->
                if (result is DataResult.Success) {
                    _uiState.update { state ->
                        val items = state.categories.getValue(SocialCategory.COMMENTS).items
                        val index = items.indexOfFirst { userSocial ->
                            userSocial is ThreadComment && userSocial.comment.id == commentId
                        }

                        if (index != -1) {
                            val threadComment = items[index] as ThreadComment
                            if (threadComment.comment is ALComment) {
                                val response = result.data

                                items[index] = threadComment.copy(
                                    comment = threadComment.comment.copy(
                                        likesCount = response.likeCount,
                                        isLiked = response.isLiked
                                    )
                                )
                            }
                        }

                        state
                    }
                }
            }
        }
    }

    private inline fun UserSocialUiState.updateCategory(
        category: SocialCategory,
        transform: (SocialCategoryUiState) -> SocialCategoryUiState
    ): UserSocialUiState {
        val current = categories.getValue(category)

        return copy(
            categories = categories + (category to transform(current))
        )
    }
}