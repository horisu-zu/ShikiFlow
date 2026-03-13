package com.example.shikiflow.presentation.viewmodel.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.repository.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ThreadParams(
    val mediaId: Int? = null,
    val sort: Sort<ThreadType> = Sort(ThreadType.CREATED_AT, SortDirection.DESCENDING)
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ThreadsViewModel @Inject constructor(
    private val commentRepository: CommentRepository
): ViewModel() {

    private val _threadParams = MutableStateFlow(ThreadParams())
    val threadParams = _threadParams.asStateFlow()

    val paginatedThreads = _threadParams
        .filter { it.mediaId != null }
        .distinctUntilChanged()
        .flatMapLatest { (mediaId, threadSort) ->
            commentRepository.getPaginatedThreads(mediaId!!, threadSort)
        }
        .cachedIn(viewModelScope)

    fun setMediaId(mediaId: Int) {
        _threadParams.update { params -> params.copy(mediaId = mediaId) }
    }

    fun setSort(sort: Sort<ThreadType>) {
        _threadParams.update { params -> params.copy(sort = sort) }
    }
}