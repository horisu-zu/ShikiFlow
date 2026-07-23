package com.example.shikiflow.presentation.viewmodel.comment.editor

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.comment.MarkdownFormat
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.repository.MediaUploaderRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentEditorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val settingsRepository: SettingsRepository,
    private val commentRepository: CommentRepository,
    private val mediaUploaderRepository: MediaUploaderRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(CommentEditorUiState())
    val uiState = _uiState.asStateFlow()

    private val _commentEvent = MutableSharedFlow<CommentEvent>()
    val commentEvent = _commentEvent.asSharedFlow()

    init {
        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                _uiState.update { state ->
                    state.copy(
                        authType = authType
                    )
                }
            }.launchIn(viewModelScope)

        _uiState
            .filter { it.uri != null }
            .distinctUntilChanged { old, new ->
                old.uri == new.uri && !new.isRefreshingUpload
            }
            .onEach { state ->
                val uri = state.uri!!
                val mime = context.contentResolver.getType(uri) ?: "application/octet-stream"

                _uiState.update { state ->
                    state.copy(
                        uploadMediaState = UploadMediaState.Uploading(0f),
                        isRefreshingUpload = false
                    )
                }

                val result = mediaUploaderRepository.upload(
                    uri = uri,
                    mime = mime,
                    onProgress = { progress ->
                        _uiState.update { state ->
                            state.copy(
                                uploadMediaState = UploadMediaState.Uploading(progress)
                            )
                        }
                    }
                )

                _uiState.update { state ->
                    state.copy(
                        uploadMediaState = when (result) {
                            is DataResult.Success -> UploadMediaState.Success(result.data, state.format!!)
                            is DataResult.Error -> UploadMediaState.Error(result.message)
                            else -> state.uploadMediaState
                        }
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun publishComment(
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
                        null -> CommentEvent.Published(comment, parentCommentId)
                        else -> CommentEvent.Updated(comment)
                    }

                    _commentEvent.emit(event)
                }
            }
        }
    }

    fun deleteComment(commentId: Int) {
        viewModelScope.launch {
            commentRepository.deleteComment(commentId).let { result ->
                if (result is DataResult.Success) {
                    _commentEvent.emit(CommentEvent.Deleted(commentId))
                }
            }
        }
    }

    fun setUri(uri: Uri) {
        _uiState.update { state ->
            state.copy(
                uri = uri
            )
        }
    }

    fun setFormat(markdownFormat: MarkdownFormat) {
        _uiState.update { state ->
            state.copy(
                format = markdownFormat
            )
        }
    }

    fun toggleOfftopic() {
        _uiState.update { state ->
            state.copy(
                isOfftopic = !state.isOfftopic
            )
        }
    }

    fun retryUpload() {
        _uiState.update { state ->
            state.copy(
                isRefreshingUpload = true
            )
        }
    }

    fun resetUploadState() {
        _uiState.update { state ->
            state.copy(
                format = null,
                uri = null,
                uploadMediaState = UploadMediaState.Idle
            )
        }
    }
}