package com.example.shikiflow.presentation.viewmodel.comment.section

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.usecase.GetCommentsUseCase
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.presentation.viewmodel.comment.editor.EditorEvent
import com.example.shikiflow.utils.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommentSectionViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val settingsRepository: SettingsRepository
): UiStateViewModel<CommentSectionUiState>() {

    override val initialState: CommentSectionUiState = CommentSectionUiState()

    fun setTopicId(topicId: Int) {
        mutableUiState.update { state ->
            state.copy(topicId = topicId)
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(isRefreshing = true)
        }
    }

    fun submitComment(
        commentId: Int?,
        topicId: Int,
        parentCommentId: Int?,
        commentBody: String,
        isOfftopic: Boolean = false
    ) {
        viewModelScope.launch {
            commentRepository.publishComment(
                id = commentId,
                topicId = topicId,
                parentCommentId = parentCommentId,
                commentBody = commentBody,
                isOfftopic = isOfftopic
            ).let { result ->
                if (result is DataResult.Success) {
                    val comment = result.data
                    val event = when (commentId) {
                        null -> EditorEvent.Published(comment, parentCommentId)
                        else -> EditorEvent.Updated(comment)
                    }

                    onCommentEvent(event)
                }
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            commentRepository.deleteComment(commentId).let { result ->
                if (result is DataResult.Success) {
                    onCommentEvent(EditorEvent.Deleted(commentId))
                }
            }
        }
    }

    private fun onCommentEvent(editorEvent: EditorEvent<Comment>) {
        mutableUiState.update { state ->
            state.copy(
                comments = when (editorEvent) {
                    is EditorEvent.Published -> state.comments + editorEvent.entry
                    is EditorEvent.Updated -> {
                        state.comments.map { comment ->
                            if (comment.id == editorEvent.entry.id) editorEvent.entry else comment
                        }
                    }
                    is EditorEvent.Deleted -> state.comments.filter { it.id != editorEvent.entryId }
                }
            )
        }
    }

    init {
        mutableUiState
            .filter { state ->
                state.topicId != null
            }
            .distinctUntilChanged { old, new ->
                old.topicId == new.topicId && old.commentsCount == new.commentsCount &&
                    !new.isRefreshing
            }
            .flatMapLatest { state ->
                getCommentsUseCase(state.topicId!!, state.commentsCount)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when (result) {
                        is DataResult.Success -> {
                            state.copy(
                                comments = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        else -> {
                            state.copy(
                                isLoading = true,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)

        settingsRepository.userFlow
            .filterNotNull()
            .map { it.id }
            .distinctUntilChanged()
            .onEach { currentUserId ->
                mutableUiState.update { state ->
                    state.copy(
                        currentUserId = currentUserId
                    )
                }
            }.launchIn(viewModelScope)
    }
}