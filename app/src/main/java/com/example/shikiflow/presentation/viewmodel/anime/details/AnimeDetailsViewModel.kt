package com.example.shikiflow.presentation.viewmodel.anime.details

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.MediaDetails.Companion.replace
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.track.media.MediaShortData
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.result.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
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

    fun refresh() {
        mutableUiState.update { state ->
            state.copy(
                isRefreshing = true
            )
        }
    }

    fun refreshFavorite() {
        mutableUiState.update { state ->
            state.copy(isRefreshingFavorite = true)
        }
    }

    fun toggleFavorite(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            userRepository.toggleFavorite(animeId = id, isFavorite = isFavorite).let { result ->
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
                mediaRepository.getMediaDetails(state.mediaId!!, mediaType = MediaType.ANIME)
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
                                details = state.details?.replace(
                                    mediaDetails = result.data
                                ) ?: result.data,
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

        mutableUiState
            .filter { state ->
                state.mediaId != null
            }
            .distinctUntilChangedBy { state -> state.mediaId }
            .flatMapLatest { state ->
                mediaTracksRepository.getLocalTrack(state.mediaId!!, MediaType.ANIME)
            }
            .onEach { mediaTrack ->
                mutableUiState.update { state ->
                    state.copy(
                        userRate = mediaTrack?.track
                    )
                }
            }.launchIn(viewModelScope)

        mutableUiState
            .filter { state ->
                state.authType == AuthType.SHIKIMORI && state.userId != null && state.details != null
            }
            .distinctUntilChanged { old, new ->
                old.mediaId == new.mediaId && !new.isRefreshingFavorite
            }
            .flatMapLatest { state ->
                userRepository.getFavorites(state.userId!!, FavoriteCategory.ANIME)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when (result) {
                        is DataResult.Loading -> {
                            state.copy(
                                isRefreshingFavorite = false,
                                favoriteErrorMessage = null
                            )
                        }
                        is DataResult.Success -> {
                            state.copy(
                                details = state.details?.copy(
                                    isFavorite = result.data
                                        .map { favorite -> favorite.id }
                                        .contains(state.mediaId)
                                )
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                favoriteErrorMessage = result.message
                            )
                        }
                    }
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

        settingsRepository.userFlow
            .mapNotNull { user -> user?.id }
            .distinctUntilChanged()
            .onEach { currentUserId ->
                mutableUiState.update { state ->
                    state.copy(userId = currentUserId)
                }
            }.launchIn(viewModelScope)

        settingsRepository.userSettingsFlow
            .filterNotNull()
            .distinctUntilChangedBy { settings -> settings.scoreFormat }
            .onEach { settings ->
                mutableUiState.update { state ->
                    state.copy(
                        scoreFormat = settings.scoreFormat
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun saveUserRate(
        saveUserRate: SaveUserRate,
        mediaShortData: MediaShortData? = null
    ) {
        mediaTracksRepository.saveUserRate(
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
                else -> {
                    mutableUiState.update { state ->
                        state.copy(
                            rateUpdateState = RateUpdateState.FINISHED
                        )
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
                when (result) {
                    DataResult.Loading -> {
                        mutableUiState.update { state ->
                            state.copy(
                                rateUpdateState = RateUpdateState.LOADING
                            )
                        }
                    }
                    else -> {
                        mutableUiState.update { state ->
                            state.copy(
                                rateUpdateState = RateUpdateState.FINISHED
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }
}