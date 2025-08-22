package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.data.common.comment.CommentItem
import com.example.shikiflow.data.common.comment.CommentType
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

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getCommentTopicUseCase: GetCommentTopicUseCase,
    private val commentsRepository: CommentRepository
): ViewModel() {

    private var currentMediaId: String? = null
    private var currentCommentId: String? = null

    private val _comments = MutableStateFlow<Resource<List<CommentItem>>>(Resource.Loading())
    val comments = _comments.asStateFlow()

    private val _commentsWithReplies = MutableStateFlow<Resource<Map<CommentType, List<CommentItem>>>>(Resource.Loading())
    val commentsWithReplies = _commentsWithReplies.asStateFlow()

    private val _pagingComments = mutableMapOf<String, Flow<PagingData<CommentItem>>>()

    fun getComments(mediaId: String, page: Int = 1, limit: Int = 30) {
        viewModelScope.launch {
            if(currentMediaId == mediaId) { return@launch }

            getCommentsUseCase(mediaId, page, limit).collect { result ->
                _comments.value = result
                when (result) {
                    is Resource.Success -> {
                        Log.d("CommentViewModel", "Comments fetched successfully: ${result.data}")
                        currentMediaId = mediaId
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
        return _pagingComments.getOrPut(topicId) {
            Pager(
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
        }
    }

    fun getCommentWithReplies(commentId: String) {
        viewModelScope.launch {
            if(currentCommentId == commentId) { return@launch }

            getCommentTopicUseCase(commentId).collect { result ->
                _commentsWithReplies.value = result
                when (result) {
                    is Resource.Success -> {
                        currentCommentId = commentId
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