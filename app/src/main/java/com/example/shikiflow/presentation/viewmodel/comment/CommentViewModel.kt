package com.example.shikiflow.presentation.viewmodel.comment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.CommentType
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.usecase.GetCommentTopicUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentTopicUseCase: GetCommentTopicUseCase,
    private val commentsRepository: CommentRepository
): ViewModel() {

    private var _currentTopicId = MutableStateFlow<Int?>(null)
    private var _pagingCache: Flow<PagingData<Comment>>? = null
    private val _commentsCache = mutableMapOf<Int, Map<CommentType, List<Comment>>>()

    private val _commentsWithReplies =
        MutableStateFlow<Resource<Map<CommentType, List<Comment>>>>(Resource.Loading())
    val commentsWithReplies = _commentsWithReplies.asStateFlow()

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
        viewModelScope.launch {
            if(_commentsCache.containsKey(commentId)) {
                _commentsWithReplies.value = Resource.Success(_commentsCache[commentId])
                return@launch
            }

            getCommentTopicUseCase(commentId).collect { result ->
                _commentsWithReplies.value = result
                when (result) {
                    is Resource.Success -> {
                        _commentsCache[commentId] = result.data ?: emptyMap()
                        Log.d("CommentViewModel", "Comment topic fetched successfully: ${result.data}")
                    }
                    is Resource.Error -> {
                        Log.d("CommentViewModel", "Error fetching comment topic: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d("CommentViewModel", "Loading comment topic...")
                    }
                }
            }
        }
    }
}