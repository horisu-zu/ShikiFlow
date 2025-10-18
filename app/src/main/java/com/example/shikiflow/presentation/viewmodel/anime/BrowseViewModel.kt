package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.data.local.source.BrowsePagingSource
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.mapper.BrowseOptions
import com.example.shikiflow.domain.model.search.ScreenSearchState
import com.example.shikiflow.domain.model.settings.BrowseUiSettings
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.MangaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.usecase.GetOngoingsCalendarUseCase
import com.example.shikiflow.utils.BrowseOngoingOrder
import com.example.shikiflow.utils.BrowseUiMode
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
    private val settingsRepository: SettingsRepository,
    private val getOngoingsCalendarUseCase: GetOngoingsCalendarUseCase,
) : ViewModel() {

    private data class SearchState(
        val type: BrowseType,
        val options: BrowseOptions
    )

    private var _searchState: SearchState? = null
    private val _browseMap = mutableMapOf<BrowseType, Flow<PagingData<Browse>>>()

    private val _ongoingBrowseState = MutableStateFlow<Resource<Map<String, List<Browse>>>>(Resource.Loading())
    val ongoingBrowseState = _ongoingBrowseState.asStateFlow()

    private val _screenState = MutableStateFlow(ScreenSearchState())
    val screenState: StateFlow<ScreenSearchState> = _screenState.asStateFlow()

    val browseUiSettings = settingsRepository.browseUiSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = BrowseUiSettings()
        )

    val browseMainOngoingsState = browseUiSettings.map { it.browseOngoingOrder }
        .distinctUntilChanged()
        .flatMapLatest { order ->
            paginatedBrowse(
                type = BrowseType.AnimeBrowseType.ONGOING,
                options = BrowseOptions(
                    status = AnimeStatusEnum.ongoing,
                    order = order.orderEnum
                )
            )
        }.cachedIn(viewModelScope)

    val searchQuery = _screenState
        .map { it.query }
        .debounce(500L)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ""
        )

    fun paginatedBrowse(
        type: BrowseType = BrowseType.AnimeBrowseType.ONGOING,
        options: BrowseOptions = BrowseOptions()
    ): Flow<PagingData<Browse>> {
        val pagerFlow = {
            Pager(
                config = PagingConfig(
                    pageSize = 45,
                    enablePlaceholders = true,
                    prefetchDistance = 12,
                    initialLoadSize = 45
                ),
                pagingSourceFactory = {
                    BrowsePagingSource(
                        animeRepository = animeRepository,
                        mangaRepository = mangaRepository,
                        type = type,
                        options = options
                    )
                }
            ).flow.cachedIn(viewModelScope)
        }

        //Caching only latest options set for search state
        return if (type == BrowseType.AnimeBrowseType.SEARCH || type == BrowseType.MangaBrowseType.SEARCH) {
            val currentState = SearchState(type, options)
            if (currentState != _searchState) {
                _searchState = currentState
                pagerFlow().also { _browseMap[type] = it }
            } else {
                _browseMap.getValue(type)
            }
        } else if(type == BrowseType.AnimeBrowseType.ONGOING) {
            pagerFlow()
        } else {
            _browseMap.getOrPut(type, pagerFlow)
        }
    }

    fun getOngoingsCalendar(isRefresh: Boolean = false) {
        if(!isRefresh && _ongoingBrowseState.value is Resource.Success) return

        getOngoingsCalendarUseCase().onEach { result ->
            _ongoingBrowseState.value = result
            when(result) {
                is Resource.Loading -> {
                    Log.d("BrowseViewModel", "Loading Ongoings...")
                }
                is Resource.Success -> {
                    Log.d("BrowseViewModel", "Result Size: ${result.data?.size}")
                }
                is Resource.Error -> {
                    Log.d("BrowseViewModel", "Error Loading Ongoings: ${result.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun setBrowseUiMode(mode: BrowseUiMode) {
        viewModelScope.launch {
            settingsRepository.saveBrowseUiMode(mode)
        }
    }

    fun setBrowseOngoingOrder(order: BrowseOngoingOrder) {
        viewModelScope.launch {
            settingsRepository.saveBrowseOngoingOrder(order)
        }
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