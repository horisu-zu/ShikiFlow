package com.example.shikiflow.presentation.viewmodel.comment

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.comment.ALComment.Companion.deleteComment
import com.example.shikiflow.domain.model.comment.ALComment.Companion.findComment
import com.example.shikiflow.domain.model.comment.ALComment.Companion.updateComment
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.ShikiComment
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.PagedUiStateViewModel
import com.example.shikiflow.presentation.viewmodel.comment.editor.EditorEvent
import com.example.shikiflow.utils.result.DataResult
import com.example.shikiflow.utils.result.PagedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
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
class CommentViewModel @Inject constructor(
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
): PagedUiStateViewModel<CommentsUiState>() {

    override val initialState = CommentsUiState()

    init {
        mutableUiState
            .filter { state ->
                state.hasNextPage && state.topicId != null
            }
            .distinctUntilChanged { old, new ->
                old.page == new.page && !new.isRefreshing
            }
            .flatMapLatest { state ->
                commentRepository.getThreadComments(state.topicId!!, state.page)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    if (result is PagedResult.Success) {
                        state.comments.addAll(result.list)

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
            .filter { state ->
                state.authType == AuthType.ANILIST && state.topicId != null
            }
            .distinctUntilChangedBy { state -> state.topicId }
            .flatMapLatest { state ->
                commentRepository.getThread(state.topicId!!)
            }.onEach { result ->
                mutableUiState
                    .update { state ->
                        when (result) {
                            is DataResult.Success -> {
                                state.copy(
                                    isLoadingThread = false,
                                    thread = result.data
                                )
                            }
                            is DataResult.Error -> {
                                state.copy(
                                    isLoadingThread = false,
                                    errorMessage = result.message
                                )
                            }
                            else -> {
                                state.copy(
                                    isLoadingThread = true
                                )
                            }
                        }
                    }
            }.launchIn(viewModelScope)

        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                mutableUiState.update { state ->
                    state.copy(
                        authType = authType
                    )
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

    fun setTopicId(topicId: Int) {
        mutableUiState.update { state ->
            state.copy(topicId = topicId)
        }
    }

    fun selectComment(commentId: Int) {
        mutableUiState.update { state ->
            state.copy(
                navState = state.navState + commentId
            )
        }
    }

    fun removeCommentFromStack() {
        mutableUiState.update { state ->
            state.copy(
                navState = state.navState.dropLast(1)
            )
        }
    }

    fun toggleCommentLike(commentId: Int) {
        viewModelScope.launch {
            userRepository.toggleLike(commentId, LikeableType.COMMENT).let { result ->
                if (result is DataResult.Success) {
                    val response = result.data

                    mutableUiState.update { state ->
                        val index = state.comments.indexOfFirst { comment ->
                            (comment as ALComment).findComment(commentId) != null
                        }

                        if (index != -1) {
                            val root = state.comments[index] as ALComment

                            state.comments[index] = root.updateComment(commentId) { comment ->
                                comment.copy(
                                    likesCount = response.likeCount,
                                    isLiked = response.isLiked
                                )
                            }
                        }

                        state
                    }
                }
            }
        }
    }

    fun toggleThreadLike(threadId: Int) {
        viewModelScope.launch {
            userRepository.toggleLike(threadId, LikeableType.THREAD).let { result ->
                if (result is DataResult.Success) {
                    val response = result.data

                    mutableUiState.update { state ->
                        state.copy(
                            thread = state.thread?.copy(
                                isLiked = response.isLiked,
                                likeCount = response.likeCount
                            )
                        )
                    }
                }
            }
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

                    onEvent(event)
                }
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            commentRepository.deleteComment(commentId).let { result ->
                if (result is DataResult.Success) {
                    onEvent(EditorEvent.Deleted(commentId))
                }
            }
        }
    }

    private fun onEvent(event: EditorEvent<Comment>) {
        mutableUiState.update { state ->
            when (event) {
                is EditorEvent.Published -> when (event.entry) {
                    is ALComment -> if (event.parentEntryId != null) {
                        val index = state.comments.indexOfFirst { comment ->
                            (comment as ALComment).findComment(event.parentEntryId) != null
                        }

                        if (index != -1) {
                            val root = state.comments[index] as ALComment

                            state.comments[index] = root.updateComment(event.parentEntryId) { comment ->
                                comment.copy(childComments = comment.childComments + event.entry)
                            }
                        }
                    } else state.comments.add(0, event.entry)

                    is ShikiComment -> state.comments.add(0, event.entry)
                }
                is EditorEvent.Updated -> {
                    when (val updatedComment = event.entry) {
                        is ShikiComment -> {
                            val index = state.comments.indexOfFirst { it.id == updatedComment.id }

                            if (index != -1) {
                                state.comments[index] = updatedComment
                            }
                        }
                        is ALComment -> {
                            val index = state.comments.indexOfFirst { comment ->
                                comment is ALComment && comment.findComment(updatedComment.id) != null
                            }

                            if (index != -1) {
                                val root = state.comments[index] as ALComment
                                state.comments[index] = root.updateComment(updatedComment.id) { updatedComment }
                            }
                        }
                    }
                }
                is EditorEvent.Deleted -> {
                    val index = state.comments.indexOfFirst { comment ->
                        if (comment is ALComment) {
                            comment.findComment(event.entryId) != null
                        } else comment.id == event.entryId
                    }

                    if (index != -1) {
                        val comment = state.comments[index]

                        if (comment is ALComment && comment.id != event.entryId) {
                            state.comments[index] = comment.deleteComment(event.entryId)
                        } else {
                            state.comments.removeAt(index)
                        }
                    }
                }
            }

            state
        }
    }

    fun refresh() {
        mutableUiState.update { state ->
            state.comments.clear()

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