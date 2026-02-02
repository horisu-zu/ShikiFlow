package com.example.shikiflow.presentation.viewmodel.manga

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.data.mapper.local.MangaEntityMapper.toMangaEntity
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.manga.MangaTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class MangaTracksViewModel @Inject constructor(
    private val mediaTracksRepository: MediaTracksRepository,
    private val userRepository: UserRepository
): ViewModel() {

    private val _pagingDataMap = mutableMapOf<String, Flow<PagingData<MangaTrack>>>()

    var rateUpdateState = mutableStateOf(RateUpdateState.INITIAL)
        private set

    fun getMangaTracks(status: UserRateStatus, userId: String): Flow<PagingData<MangaTrack>> {
        val key = "$userId-$status"

        return _pagingDataMap.getOrPut(key) {
            mediaTracksRepository.getMangaTracks(status, userId).cachedIn(viewModelScope)
        }
    }

    fun saveUserRate(saveUserRate: SaveUserRate) = viewModelScope.launch {
        rateUpdateState.value = RateUpdateState.LOADING

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
            rateUpdateState.value = RateUpdateState.FINISHED
        }
    }
}