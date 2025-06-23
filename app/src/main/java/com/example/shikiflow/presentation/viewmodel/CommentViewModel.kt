package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.common.comment.CommentItem
import com.example.shikiflow.domain.usecase.GetCommentsUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase
): ViewModel() {

    private var currentMediaId: String? = null
    private val _comments = MutableStateFlow<Resource<List<CommentItem>>>(Resource.Loading())
    val comments = _comments.asStateFlow()

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
}