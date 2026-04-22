package com.example.shikiflow.presentation.viewmodel.browse.side

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.browse.BrowseType
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BrowseSideViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    settingsRepository: SettingsRepository
): ViewModel() {

    private val _params = MutableStateFlow(BrowseSideParams())

    init {
        settingsRepository.authTypeFlow
            .distinctUntilChanged()
            .onEach { authType ->
                _params.update { params ->
                    params.copy(authType = authType)
                }
            }.launchIn(viewModelScope)
    }

    val sideBrowseItems = _params
        .filter { params ->
            params.browseType != null && params.authType != null
        }
        .distinctUntilChanged()
        .flatMapLatest { params ->
            mediaRepository.paginatedBrowseMedia(
                browseOptions = MediaBrowseOptions(
                    mediaType = params.browseType?.mediaType!!,
                    order = params.browseType.sort,
                    status = params.browseType.status,
                    season = params.browseType.season
                )
            )
        }.cachedIn(viewModelScope)

    fun setBrowseType(browseType: BrowseType) {
        _params.update { params ->
            params.copy(browseType = browseType)
        }
    }
}