package com.example.shikiflow.presentation.viewmodel.browse.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.browse.Browse.Companion.asBrowse
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.search.ScreenSearchState
import com.example.shikiflow.domain.repository.CharacterRepository
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.StaffRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.screen.browse.main.SearchType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class BrowseSearchViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val characterRepository: CharacterRepository,
    private val staffRepository: StaffRepository,
    private val userRepository: UserRepository,
    settingsRepository: SettingsRepository
): ViewModel(), BrowseSearchEvent {

    private val _searchState = MutableStateFlow(ScreenSearchState())
    val searchState: StateFlow<ScreenSearchState> = _searchState.asStateFlow()

    private val _params = MutableStateFlow(BrowseSearchParams())
    val params = _params.asStateFlow()

    val authType = settingsRepository.authTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    val browseItems = combine(
        _params,
        _searchState.debounce { state ->
            if(state.query.isNotBlank()) 500L else 0L
        }
    ) { params, screenState ->
        params.copy(
            mediaBrowseOptions = params.mediaBrowseOptions.copy(
                name = screenState.query
            )
        )
    }
        .distinctUntilChanged()
        .flatMapLatest { params ->
            when(params.searchType) {
                SearchType.MEDIA -> {
                    mediaRepository.paginatedBrowseMedia(
                        browseOptions = params.mediaBrowseOptions
                    ).asBrowse()
                }
                SearchType.CHARACTER -> {
                    characterRepository.searchCharacters(
                        search = params.mediaBrowseOptions.name!!
                    ).asBrowse()
                }
                SearchType.STAFF -> {
                    staffRepository.searchStaff(
                        search = params.mediaBrowseOptions.name!!
                    ).asBrowse()
                }
                SearchType.USER -> {
                    userRepository.getUsers(
                        nickname = params.mediaBrowseOptions.name!!
                    ).asBrowse()
                }
            }
        }.cachedIn(viewModelScope)

    override fun setSearchType(searchType: SearchType) {
        _params.update { params ->
            params.copy(searchType = searchType)
        }
    }

    override fun updateSearchOptions(browseOptions: MediaBrowseOptions) {
        _params.update { params ->
            params.copy(mediaBrowseOptions = browseOptions)
        }
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