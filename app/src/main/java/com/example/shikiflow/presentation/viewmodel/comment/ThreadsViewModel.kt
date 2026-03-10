package com.example.shikiflow.presentation.viewmodel.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.thread.ThreadSort
import com.example.shikiflow.domain.model.thread.ThreadSortType
import com.example.shikiflow.domain.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

private data class ThreadParamsState(
    val mediaId: Int? = null,
    val sort: ThreadSort = ThreadSort(ThreadSortType.ID, SortDirection.DESCENDING)
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ThreadsViewModel @Inject constructor(
    private val commentRepository: CommentRepository
): ViewModel() {

    private val _threadParamsState = MutableStateFlow(ThreadParamsState())

    val paginatedThreads = _threadParamsState
        .filter { it.mediaId != null }
        .distinctUntilChanged()
        .flatMapLatest { (mediaId, threadSort) ->
            commentRepository.getPaginatedThreads(mediaId!!, threadSort)
        }
        .cachedIn(viewModelScope)

    fun setMediaId(mediaId: Int) {
        _threadParamsState.update { params -> params.copy(mediaId = mediaId) }
    }

    fun setSort(sort: ThreadSort) {
        _threadParamsState.update { params -> params.copy(sort = sort) }
    }
}