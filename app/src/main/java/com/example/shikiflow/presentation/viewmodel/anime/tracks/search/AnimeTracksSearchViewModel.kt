package com.example.shikiflow.presentation.viewmodel.anime.tracks.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnimeTracksSearchViewModel @Inject constructor(
    private val mediaTracksRepository: MediaTracksRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _params = MutableStateFlow(TracksParams())

    init {
        settingsRepository.userFlow
            .filterNotNull()
            .onEach { user ->
                _params.update { params ->
                    params.copy(userId = user.id)
                }
            }.launchIn(viewModelScope)
    }

    val animeTracksItems = _params
        .filter { params ->
            params.userId != null
        }
        .flatMapLatest { params ->
            mediaTracksRepository.browseMediaTracks(
                userId = params.userId!!,
                mediaType = MediaType.ANIME,
                title = params.query,
                userRateStatus = params.userRateStatus
            )
        }.cachedIn(viewModelScope)

    fun setRateStatus(userRateStatus: UserRateStatus?) {
        _params.update { state ->
            state.copy(userRateStatus = userRateStatus)
        }
    }

    fun setQuery(query: String) {
        _params.update { state ->
            state.copy(query = query)
        }
    }
}