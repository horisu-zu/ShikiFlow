package com.example.shikiflow.presentation.viewmodel.browse.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.settings.BrowseUiMode
import com.example.shikiflow.domain.model.settings.BrowseUiSettings
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
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

    val authType = settingsRepository.authTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    val browseUiSettings = settingsRepository.browseUiSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = BrowseUiSettings()
        )

    val browseMainOngoingsState = browseUiSettings.map { it.browseOngoingOrder }
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { order ->
            mediaRepository.paginatedBrowseMedia(
                browseOptions = MediaBrowseOptions(
                    mediaType = MediaType.ANIME,
                    status = MediaStatus.ONGOING,
                    order = order
                )
            )
        }.cachedIn(viewModelScope)

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