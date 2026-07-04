package com.example.shikiflow.presentation.viewmodel.comment.tree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommentTreeViewModel @Inject constructor(
    private val commentRepository: CommentRepository
): ViewModel() {
    private val _commentIds = MutableStateFlow<Set<Int>>(emptySet())

    val comments = _commentIds
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()
        .flatMapLatest { ids ->
            commentRepository.observeComments(ids).map { list ->
                list.associateBy { comment ->
                    comment.id
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun toggleLike(commentId: Int) {
        viewModelScope.launch {
            commentRepository.toggleCommentLike(commentId)
        }
    }

    fun addCommentId(commentId: Int) {
        _commentIds.update { ids ->
            ids + commentId
        }
    }

    fun removeCommentId(commentId: Int) {
        _commentIds.update { ids ->
            ids - commentId
        }
    }
}