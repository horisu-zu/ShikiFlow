package com.example.shikiflow.presentation.viewmodel.anime.details

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.track.media.MediaShortData
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
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
    private val mediaTracksRepository: MediaTracksRepository,
    settingsRepository: SettingsRepository
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

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            userRepository.toggleFavorite(animeId = id).let { result ->
                if(result is DataResult.Success) {
                    mutableUiState.update { state ->
                        state.copy(
                            details = state.details?.copy(
                                isFavorite = !state.details.isFavorite!!
                            )
                        )
                    }
                }
            }
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

        settingsRepository.userFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { user ->
                mutableUiState.update { state ->
                    state.copy(userId = user.id)
                }
            }.launchIn(viewModelScope)

        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                mutableUiState.update { state ->
                    state.copy(authType = authType)
                }
            }.launchIn(viewModelScope)
    }

    fun saveUserRate(
        userId: Int,
        saveUserRate: SaveUserRate,
        mediaShortData: MediaShortData? = null
    ) {
        mediaTracksRepository.saveUserRate(
            userId = userId,
            entryId = saveUserRate.rateId,
            mediaId = saveUserRate.mediaId,
            malId = saveUserRate.malId,
            score = saveUserRate.score,
            progress = saveUserRate.progress,
            repeat = saveUserRate.repeat,
            status = saveUserRate.userStatus,
            mediaType = MediaType.ANIME,
            mediaShortData = mediaShortData
        ).onEach { result ->
            when (result) {
                DataResult.Loading -> {
                    mutableUiState.update { state ->
                        state.copy(
                            rateUpdateState = RateUpdateState.LOADING
                        )
                    }
                }

                is DataResult.Error -> {
                    Log.e("AnimeDetailsViewModel", "Error saving user rate: ${result.message}")
                    mutableUiState.update { state ->
                        state.copy(
                            rateUpdateState = RateUpdateState.FINISHED
                        )
                    }
                }

                is DataResult.Success -> {
                    mutableUiState.update { state ->
                        state.copy(
                            rateUpdateState = RateUpdateState.FINISHED,
                            details = state.details?.copy(
                                userRate = result.data
                            )
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }
}