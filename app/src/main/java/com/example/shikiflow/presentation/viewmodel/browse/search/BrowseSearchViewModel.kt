package com.example.shikiflow.presentation.viewmodel.browse.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.search.ScreenSearchState
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.text.isNotEmpty

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class BrowseSearchViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel(), BrowseSearchEvent {

    private val _searchState = MutableStateFlow(ScreenSearchState())
    val searchState: StateFlow<ScreenSearchState> = _searchState.asStateFlow()

    private val _options = MutableStateFlow(BrowseOptions(MediaType.ANIME))
    val options = _options.asStateFlow()

    val browseMediaItems = combine(
        _options,
        _searchState.debounce { state ->
            if(state.query.isNotBlank()) 500L else 0L
        }
    ) { params, screenState ->
        params.copy(name = screenState.query)
    }
        .distinctUntilChanged()
        .flatMapLatest { params ->
            mediaRepository.paginatedBrowseMedia(browseOptions = params)
        }.cachedIn(viewModelScope)

    override fun updateSearchOptions(browseOptions: BrowseOptions) {
        _options.update { browseOptions }
    }

    override fun onQueryChange(query: String) {
        _searchState.update { state ->
            state.copy(
                query = query
            )
        }
    }

    override fun onSearchStateChange(isActive: Boolean) {
        _searchState.update { state ->
            state.copy(
                isSearchActive = isActive
            )
        }
    }
}