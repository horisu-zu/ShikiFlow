package com.example.shikiflow.presentation.viewmodel.anime

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.data.local.source.BrowsePagingSource
import com.example.shikiflow.domain.model.mapper.BrowseOptions
import com.example.shikiflow.domain.model.mapper.BrowseParams
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.MangaRepository
import com.example.shikiflow.utils.AppSettingsManager
import com.example.shikiflow.utils.AppUiMode
import com.example.shikiflow.utils.BrowseUiMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository,
    private val appSettingsManager: AppSettingsManager
) : ViewModel() {

    private val _browseMap = mutableMapOf<BrowseKey, Flow<PagingData<Browse>>>()

    private val _browseUiMode = MutableStateFlow(BrowseUiMode.AUTO)
    val browseUiMode = _browseUiMode.asStateFlow()

    private val _appUiMode = MutableStateFlow(AppUiMode.LIST)
    val appUiMode = _appUiMode.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                appSettingsManager.appUiModeFlow.distinctUntilChanged(),
                appSettingsManager.browseUiModeFlow.distinctUntilChanged()
            ) { appUiMode, browseUiMode ->
                _appUiMode.value = appUiMode
                _browseUiMode.value = browseUiMode
            }.collect()
        }
    }

    private data class BrowseKey(
        val type: BrowseType,
        val options: BrowseOptions,
        val name: String?
    )

    fun paginatedBrowse(
        type: BrowseType = BrowseType.AnimeBrowseType.ONGOING,
        options: BrowseOptions = BrowseParams.animeParams[type] ?: BrowseOptions(),
        name: String? = null,
    ): Flow<PagingData<Browse>> {
        val key = BrowseKey(type, options, name)

        return _browseMap.getOrPut(key) {
            Pager(
                config = PagingConfig(
                    pageSize = 45,
                    enablePlaceholders = true,
                    prefetchDistance = 12,
                    initialLoadSize = 45
                ),
                pagingSourceFactory = { BrowsePagingSource(
                    animeRepository = animeRepository,
                    mangaRepository = mangaRepository,
                    name = name,
                    type = type,
                    options = options
                ) }
            ).flow.cachedIn(viewModelScope)
        }
    }

    fun setBrowseUiMode(mode: BrowseUiMode) {
        viewModelScope.launch {
            appSettingsManager.saveBrowseUiMode(mode)
        }
    }
}