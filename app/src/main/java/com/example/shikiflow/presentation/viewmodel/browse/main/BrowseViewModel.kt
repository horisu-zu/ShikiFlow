package com.example.shikiflow.presentation.viewmodel.browse.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.anime.BrowseType.Companion.getBrowseOptions
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.settings.BrowseUiMode
import com.example.shikiflow.domain.model.settings.BrowseUiSettings
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _browseMap = mutableMapOf<BrowseType, Flow<PagingData<Browse>>>()

    val browseUiSettings = settingsRepository.browseUiSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = BrowseUiSettings()
        )

    val browseMainOngoingsState = browseUiSettings.map { it.browseOngoingOrder }
        .distinctUntilChanged()
        .flatMapLatest { order ->
            Log.d("BrowseViewModel", "Ongoing Order: $order")
            paginatedBrowse(
                type = BrowseType.AnimeBrowseType.ONGOING,
                options = BrowseOptions(
                    mediaType = MediaType.ANIME,
                    status = MediaStatus.ONGOING,
                    order = order
                )
            )
        }.cachedIn(viewModelScope)

    fun paginatedBrowse(
        type: BrowseType = BrowseType.AnimeBrowseType.ONGOING,
        options: BrowseOptions = type.getBrowseOptions()
    ): Flow<PagingData<Browse>> {
        val pagerFlow = mediaRepository.paginatedBrowseMedia(
            browseType = type,
            browseOptions = options
        ).cachedIn(viewModelScope)

        return if(type == BrowseType.AnimeBrowseType.ONGOING) {
            pagerFlow
        } else {
            _browseMap.getOrPut(type) { pagerFlow }
        }
    }

    fun setBrowseUiMode(mode: BrowseUiMode) {
        viewModelScope.launch {
            settingsRepository.saveBrowseUiMode(mode)
        }
    }

    fun setBrowseOngoingOrder(order: MediaSort) {
        viewModelScope.launch {
            settingsRepository.saveBrowseOngoingOrder(order)
        }
    }
}