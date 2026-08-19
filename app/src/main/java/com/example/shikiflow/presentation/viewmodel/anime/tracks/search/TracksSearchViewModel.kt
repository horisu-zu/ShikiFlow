package com.example.shikiflow.presentation.viewmodel.anime.tracks.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.presentation.screen.main.TracksFilterType
import com.example.shikiflow.utils.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class TracksSearchViewModel @Inject constructor(
    private val mediaTracksRepository: MediaTracksRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {
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

        settingsRepository.userSettingsFlow
            .filterNotNull()
            .distinctUntilChanged { old, new ->
                old.scoreFormat == new.scoreFormat && old.customLists == new.customLists
            }
            .onEach { settings ->
                _params.update { params ->
                    params.copy(
                        scoreFormat = settings.scoreFormat,
                        customLists = settings.customLists
                    )
                }
            }.launchIn(viewModelScope)
    }

    val tracksItems = _params
        .map { it.filters }
        .filter { filters ->
            filters.mediaType != null
        }
        .flatMapLatest { filters ->
            mediaTracksRepository.browseMediaTracks(
                mediaType = filters.mediaType!!,
                title = filters.query,
                userRateStatus = filters.userRateStatus,
                sort = filters.sort,
                genres = filters.genres,
                tags = filters.tags
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
            repeat = saveUserRate.repeat,
            customLists = saveUserRate.customLists
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
        _params.update { params ->
            params.copy(
                filters = params.filters.copy(
                    userRateStatus = userRateStatus
                )
            )
        }
    }

    fun setMediaType(mediaType: MediaType) {
        _params.update { params ->
            params.copy(
                filters = params.filters.copy(
                    mediaType = mediaType
                )
            )
        }
    }

    fun setSort(sort: Sort<UserRateType>) {
        _params.update { params ->
            params.copy(
                filters = params.filters.copy(
                    sort = sort
                )
            )
        }
    }

    fun setGenre(genre: Genre) {
        _params.update { params ->
            params.copy(
                filters = params.filters.copy(
                    genres = if(params.filters.genres.contains(genre)) params.filters.genres - genre
                        else params.filters.genres + genre
                )
            )
        }
    }

    fun setTag(tag: MediaTagEnum) {
        _params.update { params ->
            params.copy(
                filters = params.filters.copy(
                    tags = if(params.filters.tags.contains(tag)) params.filters.tags - tag
                        else params.filters.tags + tag
                )
            )
        }
    }

    fun setFilterType(filterType: TracksFilterType) {
        _params.update { params ->
            params.copy(
                filters = params.filters.copy(
                    currentFilterType = filterType
                )
            )
        }
    }

    fun onQueryChange(newQuery: String) {
        _params.update { params ->
            params.copy(
                filters = params.filters.copy(
                    query = newQuery
                )
            )
        }
    }
}