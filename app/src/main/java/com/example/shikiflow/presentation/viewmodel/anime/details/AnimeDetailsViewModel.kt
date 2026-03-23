package com.example.shikiflow.presentation.viewmodel.anime.details

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.mapper.local.AnimeEntityMapper.toAnimeEntity
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.track.anime.AnimeShortData
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AnimeDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository,
    private val mediaTracksRepository: MediaTracksRepository
) : UiStateViewModel<AnimeDetailsUiState>() {

    override val initialState: AnimeDetailsUiState = AnimeDetailsUiState()

    fun setMediaId(mediaId: Int) {
        mutableUiState.update { state ->
            state.copy(mediaId = mediaId)
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(
                isRefreshing = true
            )
        }
    }

    init {
        mutableUiState
            .filter { state ->
                state.mediaId != null
            }
            .distinctUntilChanged { old, new ->
                old.mediaId == new.mediaId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                mediaRepository.getMediaDetails(state.mediaId!!, MediaType.ANIME)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when(result) {
                        is DataResult.Loading -> {
                            state.copy(
                                isLoading = true,
                                errorMessage = null,
                                isRefreshing = false
                            )
                        }
                        is DataResult.Success -> {
                            state.copy(
                                details = result.data,
                                isLoading = false
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                errorMessage = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun saveUserRate(
        userId: Int,
        saveUserRate: SaveUserRate,
        animeShortData: AnimeShortData? = null
    ) {
        viewModelScope.launch {
            try {
                mutableUiState.update { state ->
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

                mutableUiState.update { state ->
                    state.copy(
                        details = state.details?.copy(
                            userRate = result
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("AnimeDetailsViewModel", "Error saving user rate: ${e.message}")
            } finally {
                mutableUiState.update { state ->
                    state.copy(
                        rateUpdateState = RateUpdateState.FINISHED
                    )
                }
            }
        }
    }
}