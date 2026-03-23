package com.example.shikiflow.presentation.viewmodel.anime.studio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class StudioViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {

    private val _query = MutableStateFlow("")
    private val _studioParams = MutableStateFlow(StudioParams())
    val studioUiState = _studioParams.asStateFlow()

    val studioTitles = combine(
        _studioParams.filter { state ->
            state.studioId != null && state.sortType != null
        },
        _query.debounce(500L)
    ) { params, query ->
        params.copy(query = query)
    }
        .filter { state ->
            state.query.isNotEmpty()
        }
        .distinctUntilChanged()
        .flatMapLatest { state ->
            mediaRepository.getStudioMedia(
                studioId = state.studioId!!,
                search = state.query,
                order = state.sortType,
                onList = state.onUserList
            )
        }.cachedIn(viewModelScope)

    fun setStudioId(studioId: Int) {
        _studioParams.update { state ->
            state.copy(
                studioId = studioId
            )
        }
    }

    fun onUserListSearchChange(value: Boolean) {
        _studioParams.update { state ->
            state.copy(
                onUserList = value
            )
        }
    }

    fun setQuery(query: String) {
        _query.update { query }
    }

    fun setSortType(sortType: SortType) {
        _studioParams.update { state ->
            state.copy(
                sortType = sortType
            )
        }
    }
}