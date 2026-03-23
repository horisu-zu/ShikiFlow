package com.example.shikiflow.presentation.viewmodel.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.usecase.GetCommentTopicUseCase
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentTopicUseCase: GetCommentTopicUseCase,
    private val commentsRepository: CommentRepository
): ViewModel() {

    private var _currentTopicId = MutableStateFlow<Int?>(null)
    private var _pagingCache: Flow<PagingData<Comment>>? = null

    private val _repliesUiState = MutableStateFlow<Map<Int, RepliesUiState>>(emptyMap())
    val repliesUiState = _repliesUiState.asStateFlow()

    fun paginatedComments(topicId: Int): Flow<PagingData<Comment>> {
        if(_pagingCache != null && topicId == _currentTopicId.value) {
            _pagingCache?.let { cache ->
                return cache
            }
        }

        val pagerFlow = commentsRepository.getPaginatedComments(topicId)
            .cachedIn(viewModelScope)

        return pagerFlow.also { pagingData ->
            _pagingCache = pagingData
            _currentTopicId.value = topicId
        }
    }

    fun getCommentWithReplies(commentId: Int) {
        if(_repliesUiState.value[commentId]?.commentsMap?.isNotEmpty() == true) return

        getCommentTopicUseCase(commentId).onEach { result ->
            _repliesUiState.update { currentMap ->
                val currentState = currentMap[commentId] ?: RepliesUiState()

                val newState = when (result) {
                    is DataResult.Loading -> currentState.copy(
                        isLoading = true,
                        errorMessage = null
                    )
                    is DataResult.Success -> currentState.copy(
                        isLoading = false,
                        commentsMap = result.data
                    )
                    is DataResult.Error -> currentState.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }

                currentMap + (commentId to newState)
            }
        }.launchIn(viewModelScope)
    }
}