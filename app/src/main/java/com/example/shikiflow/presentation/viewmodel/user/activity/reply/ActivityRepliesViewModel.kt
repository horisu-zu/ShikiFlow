package com.example.shikiflow.presentation.viewmodel.user.activity.reply

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.user.activity.UserActivityMapper.updateLike
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.PagedUiStateViewModel
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ActivityRepliesViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
): PagedUiStateViewModel<ActivityRepliesUiState>() {

    override val initialState: ActivityRepliesUiState = ActivityRepliesUiState()

    init {
        mutableUiState
            .filter { state ->
                state.hasNextPage && state.activityId != null
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page && !new.isRefreshing
            }
            .flatMapLatest { state ->
                userRepository.getActivityReplies(state.activityId!!, state.page)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    if (result is PagedResult.Success) {
                        state.replies.addAll(result.list)

                        state.copy(
                            isLoading = false,
                            hasNextPage = result.hasNextPage
                        )
                    } else {
                        result.toUiState().copy(
                            hasNextPage = false,
                            isRefreshing = false
                        )
                    }
                }
            }.launchIn(viewModelScope)

        mutableUiState
            .mapNotNull { state -> state.activityId }
            .distinctUntilChanged()
            .flatMapLatest { activityId ->
                userRepository.getSingleActivity(activityId)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when (result) {
                        is DataResult.Loading -> {
                            state.copy(
                                isLoadingActivity = true,
                                errorMessage = null
                            )
                        }
                        is DataResult.Success -> {
                            state.copy(
                                activity = result.data,
                                isLoadingActivity = false
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                isLoadingActivity = false,
                                errorMessage = result.message
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)

        settingsRepository.userFlow
            .mapNotNull { it?.id }
            .distinctUntilChanged()
            .onEach { userId ->
                mutableUiState.update { state ->
                    state.copy(
                        currentUserId = userId
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun setId(activityId: Int) {
        mutableUiState.update { state ->
            state.copy(
                activityId = activityId
            )
        }
    }

    fun toggleLike(id: Int, type: LikeableType) {
        viewModelScope.launch {
            userRepository.toggleLike(id, type).let { result ->
                if (result is DataResult.Success) {
                    val data = result.data

                    mutableUiState.update { state ->
                        when (type) {
                            LikeableType.ACTIVITY -> {
                                state.copy(
                                    activity = state.activity?.updateLike(
                                        likeCount = data.likeCount,
                                        isLiked = data.isLiked
                                    )
                                )
                            }
                            LikeableType.ACTIVITY_REPLY -> {
                                val index = state.replies.indexOfFirst { it.id == id }

                                if (index != -1) {
                                    state.replies[index] = state.replies[index].copy(
                                        likeCount = data.likeCount,
                                        isLiked = data.isLiked
                                    )
                                }

                                state
                            }
                            else -> state
                        }
                    }
                }
            }
        }
    }

    fun refresh() {
        mutableUiState.update { state ->
            state.replies.clear()

            state.copy(
                page = 1,
                isRefreshing = true,
                hasNextPage = true
            )
        }
    }

    fun retry() {
        mutableUiState.update { state ->
            state.copy(
                isRefreshing = true,
                hasNextPage = true
            )
        }
    }
}