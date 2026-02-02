package com.example.shikiflow.presentation.viewmodel.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.usecase.GetCommentsUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CommentSectionViewModel @Inject constructor(
    private val getCommentsUseCase: GetCommentsUseCase
): ViewModel() {

    private val _currentTopicId = MutableStateFlow<Int?>(null)

    val previewComments = _currentTopicId
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { topicId ->
            getCommentsUseCase(topicId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Resource.Loading()
        )

    fun setTopicId(topicId: Int) {
        _currentTopicId.value = topicId
    }
    /*fun getCommentsPreview(
        topicId: Int,
        sortDirection: SortDirection = SortDirection.DESCENDING,
        limit: Int = 5,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            if(_currentTopicId == topicId && !isRefresh) { return@launch }

            getCommentsUseCase(topicId, sortDirection, limit).collect { result ->
                _comments.value = result
                when (result) {
                    is Resource.Success -> {
                        _currentTopicId = topicId
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
    }*/
}