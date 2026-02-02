package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.mapper.local.AnimeEntityMapper.toAnimeEntity
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.track.anime.AnimeShortData
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository,
    private val mediaTracksRepository: MediaTracksRepository
) : ViewModel() {

    private var currentId: Int? = null

    private val _animeDetails = MutableStateFlow<Resource<MediaDetails>>(Resource.Loading())
    val animeDetails = _animeDetails.asStateFlow()

    var rateUpdateState = mutableStateOf(RateUpdateState.INITIAL)
        private set

    var isRefreshing = mutableStateOf(false)
        private set

    fun getAnimeDetails(id: Int, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh && currentId != id) {
                _animeDetails.value = Resource.Loading()
            } else if(!isRefresh) {
                return@launch
            } else {
                isRefreshing.value = true
            }

            val result = mediaRepository.getMediaDetails(id, mediaType = MediaType.ANIME)

            result.fold(
                onSuccess = { mediaDetails ->
                    _animeDetails.value = Resource.Success(mediaDetails)
                    currentId = id
                },
                onFailure = { exception ->
                    _animeDetails.value = Resource.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }

    fun saveUserRate(
        userId: Int,
        saveUserRate: SaveUserRate,
        animeShortData: AnimeShortData? = null
    ) {
        viewModelScope.launch {
            try {
                rateUpdateState.value = RateUpdateState.LOADING

                val result = userRepository.saveUserRate(
                    userId = userId,
                    entryId = saveUserRate.rateId,
                    mediaId = saveUserRate.mediaId,
                    score = saveUserRate.score,
                    progress = saveUserRate.progress,
                    repeat = saveUserRate.repeat,
                    status = saveUserRate.userStatus,
                    mediaType = MediaType.ANIME
                )

                mediaTracksRepository.updateAnimeTrack(
                    animeTrack = result.toAnimeEntity(),
                    animeShortData = if(saveUserRate.rateId != null ) null else animeShortData
                )

                _animeDetails.update { resource ->
                    if (resource is Resource.Success) {
                        Resource.Success(resource.data?.copy(userRate = result))
                    } else { resource }
                }
            } catch (e: Exception) {
                Log.e("AnimeDetailsViewModel", "Error saving user rate: ${e.message}")
            } finally {
                rateUpdateState.value = RateUpdateState.FINISHED
            }
        }
    }
}