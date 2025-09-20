package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.comment.CommentItem
import com.example.shikiflow.domain.model.comment.CommentType
import com.example.shikiflow.data.local.source.CommentPagingSource
import com.example.shikiflow.domain.repository.CommentRepository
import com.example.shikiflow.domain.usecase.GetCommentTopicUseCase
import com.example.shikiflow.domain.usecase.GetCommentsUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.mutableMapOf

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getCommentTopicUseCase: GetCommentTopicUseCase,
    private val commentsRepository: CommentRepository
): ViewModel() {

    private var _currentTopicId: String? = null
    private var _paginatedTopicId: String? = null
    private var _pagingCache: Flow<PagingData<CommentItem>>? = null
    private val _commentsCache = mutableMapOf<String, Map<CommentType, List<CommentItem>>>()

    private val _comments = MutableStateFlow<Resource<List<CommentItem>>>(Resource.Loading())
    val comments = _comments.asStateFlow()

    private val _commentsWithReplies = MutableStateFlow<Resource<Map<CommentType, List<CommentItem>>>>(Resource.Loading())
    val commentsWithReplies = _commentsWithReplies.asStateFlow()

    fun getComments(topicId: String, page: Int = 1, limit: Int = 30) {
        viewModelScope.launch {
            if(_currentTopicId == topicId) { return@launch }

            getCommentsUseCase(topicId, page, limit).collect { result ->
                _comments.value = result
                when (result) {
                    is Resource.Success -> {
                        _currentTopicId = topicId
                        _commentsCache.clear()
                        Log.d("CommentViewModel", "Comments fetched successfully: ${result.data}")
                    }
                    is Resource.Error -> {
                        Log.d("CommentViewModel", "Error fetching comments: ${result.message}")
                    }
                    is Resource.Loading -> {
                        Log.d("CommentViewModel", "Loading comments...")
                    }
                }
            }
        }
    }

    fun paginatedComments(topicId: String): Flow<PagingData<CommentItem>> {
        if(_pagingCache != null && topicId == _paginatedTopicId) {
            _pagingCache?.let { cache ->
                return cache
            }
        }

        val pagerFlow = Pager(
            config = PagingConfig(
                pageSize = 15, //30 max for comments
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 15
            ),
            pagingSourceFactory = { CommentPagingSource(
                commentsRepository,
                topicId
            ) }
        ).flow.cachedIn(viewModelScope)

        return pagerFlow.also { pagingData ->
            _pagingCache = pagingData
            _paginatedTopicId = topicId
        }
    }

    fun getCommentWithReplies(commentId: String) {
        viewModelScope.launch {
            if(_commentsCache.containsKey(commentId)) {
                _commentsWithReplies.value = Resource.Success(_commentsCache[commentId])
                return@launch
            }

            getCommentTopicUseCase(commentId).collect { result ->
                _commentsWithReplies.value = result
                when (result) {
                    is Resource.Success -> {
                        _commentsCache.put(commentId, result.data ?: emptyMap())
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