package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.data.mapper.local.AnimeEntityMapper.toAnimeEntity
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.domain.model.settings.AppUiMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class AnimeTracksViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val mediaTracksRepository: MediaTracksRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _pagingDataMap = mutableMapOf<String, Flow<PagingData<AnimeTrack>>>()

    var rateUpdateState = mutableStateOf(RateUpdateState.INITIAL)
        private set

    val appUiMode: StateFlow<AppUiMode> = settingsRepository.settingsFlow
        .map { it.appUiMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AppUiMode.LIST
        )

    fun getAnimeTracks(status: UserRateStatus, userId: String): Flow<PagingData<AnimeTrack>> {
        val key = "$userId-$status"

        return _pagingDataMap.getOrPut(key) {
            mediaTracksRepository.getAnimeTracks(status, userId).cachedIn(viewModelScope)
        }
    }

    fun saveUserRate(saveUserRate: SaveUserRate) = viewModelScope.launch {
        rateUpdateState.value = RateUpdateState.LOADING

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
            rateUpdateState.value = RateUpdateState.FINISHED
        }
    }
}