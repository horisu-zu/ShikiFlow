package com.example.shikiflow.presentation.viewmodel.anime.tracks.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private val _query = MutableStateFlow("")
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

    val animeTracksItems = combine(
        _params.filter { it.userId != null },
        _query.debounce { query ->
            if(query.isNotBlank()) 500L else 0L
        }
    ) { params, query ->
        params.copy(query = query)
    }
        .distinctUntilChanged()
        .flatMapLatest { params ->
            mediaTracksRepository.getBrowseTracks(
                userId = params.userId,
                title = params.query,
                userRateStatus = params.userRateStatus
            )
        }.cachedIn(viewModelScope)

    fun setRateStatus(userRateStatus: UserRateStatus?) {
        _params.update { params ->
            params.copy(userRateStatus = userRateStatus)
        }
    }

    fun setQuery(query: String) {
        _query.update { query }
    }
}