package com.example.shikiflow.presentation.viewmodel.user.activity.reply

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.user.activity.ActivityReply
import com.example.shikiflow.domain.model.user.activity.UserActivityMapper.updateLike
import com.example.shikiflow.domain.repository.ActivityRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.PagedUiStateViewModel
import com.example.shikiflow.presentation.viewmodel.comment.editor.EditorEvent
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val activityRepository: ActivityRepository,
    private val settingsRepository: SettingsRepository
): PagedUiStateViewModel<ActivityRepliesUiState>() {

    override val initialState: ActivityRepliesUiState = ActivityRepliesUiState()

    private val _event = MutableSharedFlow<EditorEvent<ActivityReply>>()
    val event = _event.asSharedFlow()

    init {
        mutableUiState
            .filter { state ->
                state.hasNextPage && state.activityId != null
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page && !new.isRefreshing
            }
            .flatMapLatest { state ->
                activityRepository.getActivityReplies(state.activityId!!, state.page)
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
                activityRepository.getSingleActivity(activityId)
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
            activityRepository.toggleLike(id, type).let { result ->
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

    fun submitReply(
        activityId: Int,
        id: Int?,
        textBody: String
    ) {
        viewModelScope.launch {
            activityRepository.submitActivityReply(
                id = id,
                activityId = activityId,
                body = textBody
            ).let { result ->
                if (result is DataResult.Success) {
                    val reply = result.data
                    val editorEvent = when (id) {
                        null -> EditorEvent.Published(entry = reply, parentEntryId = null)
                        else -> EditorEvent.Updated(entry = reply)
                    }

                    onEvent(editorEvent)
                }
            }
        }
    }

    fun deleteReply(id: Int) {
        viewModelScope.launch {
            activityRepository.deleteActivityReply(id).let { result ->
                if (result is DataResult.Success) {
                    onEvent(EditorEvent.Deleted(id))
                }
            }
        }
    }

    private fun onEvent(event: EditorEvent<ActivityReply>) {
        viewModelScope.launch {
            _event.emit(event)

            mutableUiState.update { state ->
                when (event) {
                    is EditorEvent.Published -> {
                        state.replies.add(event.entry)
                    }
                    is EditorEvent.Updated -> {
                        val index = state.replies.indexOfFirst { it.id == event.entry.id }

                        if (index != -1) {
                            state.replies[index] = event.entry
                        }
                    }
                    is EditorEvent.Deleted -> {
                        val index = state.replies.indexOfFirst { it.id == event.entryId }

                        if (index != -1) {
                            state.replies.removeAt(index)
                        }
                    }
                }

                state
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