package com.example.shikiflow.presentation.viewmodel.anime

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.search.ScreenSearchState
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class BrowseSearchViewModel @Inject constructor(
    private val mediaRepository: MediaRepository
): ViewModel() {

    private var _searchState: SearchState? = null
    private val _browseMap = mutableMapOf<MediaType, Flow<PagingData<Browse>>>()

    private val _screenState = MutableStateFlow(ScreenSearchState())
    val screenState: StateFlow<ScreenSearchState> = _screenState.asStateFlow()

    var searchOptions = mutableStateOf(BrowseOptions(mediaType = MediaType.ANIME))
        private set

    val searchQuery = _screenState
        .map { it.query }
        .debounce(500L)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun paginatedBrowseSearch(
        options: BrowseOptions
    ): Flow<PagingData<Browse>> {
        val currentState = SearchState(options.mediaType, options)

        val pagerFlow = mediaRepository.browseMedia(browseOptions = options).cachedIn(viewModelScope)

        return if(_searchState != currentState) {
            _searchState = currentState
            pagerFlow.also { _browseMap[options.mediaType] = it }
        } else {
            _browseMap.getValue(options.mediaType)
        }
    }

    fun updateSearchOptions(newOptions: BrowseOptions) {
        searchOptions.value = newOptions
    }

    fun clearSearchOptions(mediaType: MediaType) {
        searchOptions.value = BrowseOptions(mediaType)
    }

    fun onQueryChange(newQuery: String) {
        _screenState.update { it.copy(query = newQuery) }
    }

    fun onSearchActiveChange(isActive: Boolean) {
        _screenState.update { it.copy(isSearchActive = isActive) }
    }

    fun exitSearchState() {
        _screenState.update { it.copy(
            isSearchActive = false,
            query = ""
        ) }
    }
}

private data class SearchState(
    val type: MediaType,
    val options: BrowseOptions
)