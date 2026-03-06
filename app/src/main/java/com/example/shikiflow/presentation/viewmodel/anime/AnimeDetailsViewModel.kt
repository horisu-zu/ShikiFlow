package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnimeDetailsUiState(
    val details: MediaDetails? = null,
    val rateUpdateState: RateUpdateState = RateUpdateState.INITIAL,
    val isRefreshing: Boolean = false,
    val isLoading: Boolean = true,
    val detailsError: String? = null
)

@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository,
    private val mediaTracksRepository: MediaTracksRepository
) : ViewModel() {

    private val _animeDetails = MutableStateFlow(AnimeDetailsUiState())
    val animeDetails = _animeDetails.asStateFlow()

    fun getAnimeDetails(id: Int, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (_animeDetails.value.details == null) {
                _animeDetails.update { state ->
                    state.copy(
                        isLoading = true
                    )
                }
            } else if(!isRefresh) {
                return@launch
            } else {
                _animeDetails.update { state ->
                    state.copy(
                        isRefreshing = true
                    )
                }
            }

            val result = mediaRepository.getMediaDetails(id, mediaType = MediaType.ANIME)
            Log.d("AnimeDetailsViewModel", "Result: $result")

            result.fold(
                onSuccess = { mediaDetails ->
                    _animeDetails.update { state ->
                        state.copy(
                            details = mediaDetails,
                            detailsError = null,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }
                },
                onFailure = { exception ->
                    _animeDetails.update { state ->
                        state.copy(
                            detailsError = exception.message ?: "Unknown error",
                            isLoading = false
                        )
                    }
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
                _animeDetails.update { state ->
                    state.copy(
                        rateUpdateState = RateUpdateState.LOADING
                    )
                }

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

                _animeDetails.update { state ->
                    state.copy(
                        details = state.details?.copy(
                            userRate = result
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("AnimeDetailsViewModel", "Error saving user rate: ${e.message}")
            } finally {
                _animeDetails.update { state ->
                    state.copy(
                        rateUpdateState = RateUpdateState.FINISHED
                    )
                }
            }
        }
    }
}