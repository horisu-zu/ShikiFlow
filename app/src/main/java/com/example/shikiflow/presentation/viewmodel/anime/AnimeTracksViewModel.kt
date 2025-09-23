package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.model.track.anime.AnimeTrack
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack.Companion.toEntity
import com.example.shikiflow.domain.model.tracks.UserRateRequest
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.AppUiMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class AnimeTracksViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val animeTracksRepository: AnimeTracksRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _pagingDataMap = mutableMapOf<UserRateStatusEnum, Flow<PagingData<AnimeTrack>>>()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _updateEvent = MutableSharedFlow<Unit>()
    val updateEvent = _updateEvent.asSharedFlow()

    val appUiMode: StateFlow<AppUiMode> = settingsRepository.settingsFlow
        .map { it.appUiMode }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = AppUiMode.LIST
        )

    fun getAnimeTracks(status: UserRateStatusEnum): Flow<PagingData<AnimeTrack>> {
        return _pagingDataMap.getOrPut(status) {
            animeTracksRepository.getAnimeTracks(status).cachedIn(viewModelScope)
        }
    }

    fun updateUserRate(
        id: Long,
        status: Int,
        score: Int,
        progress: Int,
        rewatches: Int
    ) = viewModelScope.launch {
        _isUpdating.value = true

        try {
            val request = UserRateRequest(
                status = UserRateStatusConstants.convertToApiStatus(status),
                score = score.takeIf { it > 0 },
                rewatches = rewatches,
                episodes = progress
            )

            val result = userRepository.updateUserRate(id, request)

            animeTracksRepository.updateAnimeTrack(result.toEntity())
        } catch (e: Exception) {
            Log.e("AnimeTracksViewModel", "Error updating user rate", e)
        } finally {
            _updateEvent.emit(Unit)
            _isUpdating.value = false
        }
    }
}