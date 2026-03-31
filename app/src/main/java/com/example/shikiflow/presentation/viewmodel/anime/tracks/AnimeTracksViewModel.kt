package com.example.shikiflow.presentation.viewmodel.anime.tracks

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import com.example.shikiflow.data.mapper.local.AnimeEntityMapper.toAnimeEntity
import com.example.shikiflow.domain.model.settings.AppUiMode
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.viewmodel.manga.tracks.MediaTracksParams
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnimeTracksViewModel @Inject constructor(
    settingsRepository: SettingsRepository,
    private val mediaTracksRepository: MediaTracksRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _params = MutableStateFlow(MediaTracksParams())
    val params = _params.asStateFlow()

    val appUiMode: StateFlow<AppUiMode> = settingsRepository.settingsFlow
        .map { it.appUiMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AppUiMode.LIST
        )

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

    fun setStatus(userRateStatus: UserRateStatus) {
        _params.update { params ->
            params.copy(
                userRateStatus = userRateStatus
            )
        }
    }

    val animeTracks = UserRateStatus.entries.associateWith { userRateStatus ->
        _params
            .filter { params ->
                params.userId != null && userRateStatus == params.userRateStatus
            }
            .distinctUntilChanged { old, new ->
                old.userId == new.userId && old.userRateStatus == new.userRateStatus
            }
            .flatMapLatest { params ->
                mediaTracksRepository.getAnimeTracks(userRateStatus, params.userId)
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
                mediaType = MediaType.ANIME,
                status = saveUserRate.userStatus,
                score = saveUserRate.score,
                progress = saveUserRate.progress,
                repeat = saveUserRate.repeat
            )

            mediaTracksRepository.updateAnimeTrack(result.toAnimeEntity())
        } catch (e: Exception) {
            Log.e("AnimeTracksViewModel", "Error updating user rate", e)
        } finally {
            _params.update { params ->
                params.copy(rateUpdateState = RateUpdateState.FINISHED)
            }
        }
    }
}