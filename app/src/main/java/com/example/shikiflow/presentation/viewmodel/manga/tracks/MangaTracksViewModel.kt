package com.example.shikiflow.presentation.viewmodel.manga.tracks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import com.example.shikiflow.data.mapper.local.MangaEntityMapper.toMangaEntity
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class MangaTracksViewModel @Inject constructor(
    private val mediaTracksRepository: MediaTracksRepository,
    private val userRepository: UserRepository,
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
                mediaTracksRepository.getMangaTracks(userRateStatus, params.userId)
            }.cachedIn(viewModelScope)
    }

    fun saveUserRate(saveUserRate: SaveUserRate) = viewModelScope.launch {
        _params.update { params ->
            params.copy(rateUpdateState = RateUpdateState.LOADING)
        }

        try {
            val result = userRepository.saveUserRate(
                entryId = saveUserRate.rateId,
                mediaId = saveUserRate.mediaId,
                mediaType = MediaType.MANGA,
                status = saveUserRate.userStatus,
                score = saveUserRate.score,
                progress = saveUserRate.progress,
                progressVolumes = saveUserRate.progressVolumes,
                repeat = saveUserRate.repeat
            )

            mediaTracksRepository.updateMangaTrack(result.toMangaEntity())
        } catch (e: Exception) {
            Log.e("MangaTracksViewModel", "Error updating user rate", e)
        } finally {
            _params.update { params ->
                params.copy(rateUpdateState = RateUpdateState.FINISHED)
            }
        }
    }
}