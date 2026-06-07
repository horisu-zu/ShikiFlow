package com.example.shikiflow.presentation.viewmodel.anime.tracks.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.screen.main.TracksFilterType
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
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
class TracksSearchViewModel @Inject constructor(
    private val mediaTracksRepository: MediaTracksRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {
    private val _query = MutableStateFlow("")

    private val _params = MutableStateFlow(TracksParams())
    val params = _params.asStateFlow()

    private val _rateUpdateState = MutableStateFlow(RateUpdateState.INITIAL)
    val rateUpdateState = _rateUpdateState.asStateFlow()

    init {
        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                _params.update { params ->
                    params.copy(authType = authType)
                }
            }.launchIn(viewModelScope)
    }

    val animeTracksItems = combine(
        _params,
        _query
    ) { params, query ->
        params.copy(
            query = query
        )
    }
        .filter { params ->
            params.mediaType != null
        }
        .flatMapLatest { params ->
            mediaTracksRepository.browseMediaTracks(
                mediaType = params.mediaType!!,
                title = params.query,
                userRateStatus = params.userRateStatus,
                sort = params.sort,
                genres = params.genres
            )
        }.cachedIn(viewModelScope)

    fun saveUserRate(saveUserRate: SaveUserRate) {
        mediaTracksRepository.saveUserRate(
            entryId = saveUserRate.rateId,
            mediaId = saveUserRate.mediaId,
            malId = saveUserRate.malId,
            mediaType = MediaType.MANGA,
            status = saveUserRate.userStatus,
            score = saveUserRate.score,
            progress = saveUserRate.progress,
            progressVolumes = saveUserRate.progressVolumes,
            repeat = saveUserRate.repeat
        ).onEach { result ->
            _rateUpdateState.update {
                when(result) {
                    is DataResult.Loading -> {
                        RateUpdateState.LOADING
                    }
                    is DataResult.Error -> {
                        Log.d("TracksSearchViewModel", "Error: ${result.message}")
                        RateUpdateState.FINISHED
                    }
                    is DataResult.Success -> {
                        RateUpdateState.FINISHED
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun deleteUserRate(
        entryId: Int,
        mediaId: Int,
        malId: Int?,
        mediaType: MediaType
    ) {
        mediaTracksRepository.deleteUserRate(entryId, mediaId, malId, mediaType)
            .onEach { result ->
                _rateUpdateState.update {
                    when(result) {
                        is DataResult.Loading -> {
                            RateUpdateState.LOADING
                        }
                        is DataResult.Error -> {
                            Log.d("TracksSearchViewModel", "Error: ${result.message}")
                            RateUpdateState.FINISHED
                        }
                        is DataResult.Success -> {
                            RateUpdateState.FINISHED
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun setRateStatus(userRateStatus: UserRateStatus?) {
        _params.update { state ->
            state.copy(userRateStatus = userRateStatus)
        }
    }

    fun setMediaType(mediaType: MediaType) {
        _params.update { params ->
            params.copy(mediaType = mediaType)
        }
    }

    fun setSort(sort: Sort<UserRateType>) {
        _params.update { params ->
            params.copy(sort = sort)
        }
    }

    fun setGenre(genre: Genre) {
        _params.update { params ->
            params.copy(
                genres = if(params.genres.contains(genre)) params.genres - genre
                    else params.genres + genre
            )
        }
    }

    fun setFilterType(filterType: TracksFilterType) {
        _params.update { params ->
            params.copy(
                currentFilterType = filterType
            )
        }
    }

    fun onQueryChange(newQuery: String) {
        _query.update { newQuery }
    }
}