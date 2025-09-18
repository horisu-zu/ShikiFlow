package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.data.local.source.BrowsePagingSource
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.mapper.BrowseOptions
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.MangaRepository
import com.example.shikiflow.domain.usecase.GetOngoingsCalendarUseCase
import com.example.shikiflow.utils.AppSettingsManager
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.BrowseOngoingOrder
import com.example.shikiflow.utils.BrowseUiMode
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
    private val appSettingsManager: AppSettingsManager,
    private val getOngoingsCalendarUseCase: GetOngoingsCalendarUseCase,
) : ViewModel() {

    private data class SearchState(
        val query: String,
        val type: BrowseType,
        val options: BrowseOptions
    )

    private var _searchState: SearchState? = null
    private val _browseMap = mutableMapOf<BrowseType, Flow<PagingData<Browse>>>()

    private val _ongoingBrowseState = MutableStateFlow<Resource<Map<String, List<Browse>>>>(Resource.Loading())
    val ongoingBrowseState = _ongoingBrowseState.asStateFlow()

    private val _browseUiMode = MutableStateFlow(BrowseUiMode.AUTO)
    val browseUiMode = _browseUiMode.asStateFlow()

    private val _appUiMode = MutableStateFlow(AppUiMode.LIST)
    val appUiMode = _appUiMode.asStateFlow()

    private val _browseOngoingMode = MutableStateFlow(BrowseOngoingOrder.RANKED)
    val browseOngoingMode = _browseOngoingMode.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                appSettingsManager.appUiModeFlow.distinctUntilChanged(),
                appSettingsManager.browseUiModeFlow.distinctUntilChanged(),
                appSettingsManager.browseOngoingOrderFlow.distinctUntilChanged()
            ) { appUiMode, browseUiMode, browseOngoingMode ->
                _appUiMode.value = appUiMode
                _browseUiMode.value = browseUiMode
                _browseOngoingMode.value = browseOngoingMode
            }.collect()
        }
    }

    fun paginatedBrowse(
        type: BrowseType = BrowseType.AnimeBrowseType.ONGOING,
        options: BrowseOptions = BrowseOptions(),
        name: String = "",
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
                        name = name,
                        type = type,
                        options = options
                    )
                }
            ).flow.cachedIn(viewModelScope)
        }

        //Caching only latest options set for search state
        return if (type == BrowseType.AnimeBrowseType.SEARCH || type == BrowseType.MangaBrowseType.SEARCH) {
            val currentState = SearchState(name, type, options)
            if (currentState != _searchState) {
                _searchState = currentState
                pagerFlow().also { _browseMap[type] = it }
            } else {
                _browseMap.getValue(type)
            }
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
            appSettingsManager.saveBrowseUiMode(mode)
        }
    }

    fun setBrowseOngoingOrder(order: BrowseOngoingOrder) {
        viewModelScope.launch {
            appSettingsManager.saveBrowseOngoingOrder(order)
        }
    }
}