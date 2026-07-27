package com.example.shikiflow.presentation.viewmodel.user.activity

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.user.activity.ActivityType
import com.example.shikiflow.domain.model.user.activity.UserActivityMapper.updateLike
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
class UserActivityViewModel @Inject constructor(
    private val userRepository: UserRepository
): PagedUiStateViewModel<UserActivityUiState>() {

    override val initialState: UserActivityUiState = UserActivityUiState()

    init {
        mutableUiState
            .filter { state ->
                state.hasNextPage && state.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page && !new.isRefreshing
            }
            .flatMapLatest { state ->
                userRepository.getUserActivity(
                    userId = state.userId!!,
                    page = state.page
                )
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    if (result is PagedResult.Success) {
                        state.items.addAll(result.list)

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
            .mapNotNull { state -> state.userId }
            .distinctUntilChanged()
            .onEach {
                refresh()
            }.launchIn(viewModelScope)
    }

    fun setId(userId: Int) {
        mutableUiState.update { state ->
            state.copy(
                userId = userId
            )
        }
    }

    fun toggleLike(activityId: Int) {
        viewModelScope.launch {
            userRepository.toggleLike(activityId, ActivityType.ACTIVITY).let { result ->
                if (result is DataResult.Success) {
                    mutableUiState.update { state ->
                        val like = result.data
                        val index = state.items.indexOfFirst { it.id == activityId }

                        if (index != -1) {
                            state.items[index] = state.items[index].updateLike(
                                likeCount = like.likeCount,
                                isLiked = like.isLiked
                            )
                        }

                        state
                    }
                }
            }
        }
    }

    fun refresh() {
        mutableUiState.update { state ->
            state.items.clear()

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