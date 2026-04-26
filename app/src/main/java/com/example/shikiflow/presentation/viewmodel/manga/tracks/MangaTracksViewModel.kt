package com.example.shikiflow.presentation.viewmodel.manga.tracks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class MangaTracksViewModel @Inject constructor(
    private val mediaTracksRepository: MediaTracksRepository,
    settingsRepository: SettingsRepository
): ViewModel() {

    private val _params = MutableStateFlow(MediaTracksParams())
    val params = _params.asStateFlow()

    init {
        settingsRepository.userFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { user ->
                _params.update { params ->
                    params.copy(
                        userId = user.id
                    )
                }
            }.launchIn(viewModelScope)
    }

    val mangaTracks = UserRateStatus.entries.associateWith { userRateStatus ->
        _params
            .filter { params ->
                params.userId != null
            }
            .distinctUntilChangedBy { params ->
                params.userId
            }
            .flatMapLatest { params ->
                mediaTracksRepository.getMediaTracks(userRateStatus, params.userId, MediaType.MANGA)
            }.cachedIn(viewModelScope)
    }

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
            _params.update { params ->
                when(result) {
                    is DataResult.Loading -> {
                        params.copy(rateUpdateState = RateUpdateState.LOADING)
                    }
                    is DataResult.Error -> {
                        Log.d("MangaTracksViewModel", "Error: ${result.message}")
                        params.copy(rateUpdateState = RateUpdateState.FINISHED)
                    }
                    is DataResult.Success -> {
                        params.copy(rateUpdateState = RateUpdateState.FINISHED)
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
                _params.update { params ->
                    when(result) {
                        is DataResult.Loading -> {
                            params.copy(rateUpdateState = RateUpdateState.LOADING)
                        }
                        is DataResult.Error -> {
                            Log.d("MangaTracksViewModel", "Error: ${result.message}")
                            params.copy(rateUpdateState = RateUpdateState.FINISHED)
                        }
                        is DataResult.Success -> {
                            params.copy(rateUpdateState = RateUpdateState.FINISHED)
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}