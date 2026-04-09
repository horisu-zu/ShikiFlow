package com.example.shikiflow.presentation.viewmodel.manga.details

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.mapper.local.MediaTrackMapper.toMediaEntity
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.track.media.MediaShortData
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.domain.usecase.GetMangaDexUseCase
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
class MangaDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository,
    private val mediaTracksRepository: MediaTracksRepository,
    private val getMangaDexUseCase: GetMangaDexUseCase,
    settingsRepository: SettingsRepository
): UiStateViewModel<MangaDetailsUiState>() {

    override val initialState: MangaDetailsUiState = MangaDetailsUiState()

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

    fun onMangaDexRefresh() {
        mutableUiState.update { state ->
            state.copy(
                mangaDexUiState = state.mangaDexUiState.copy(
                    isRefreshing = true
                )
            )
        }
    }

    init {
        //Details
        mutableUiState
            .filter { state ->
                state.mediaId != null
            }
            .distinctUntilChanged { old, new ->
                old.mediaId == new.mediaId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                mediaRepository.getMediaDetails(state.mediaId!!, MediaType.MANGA)
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

        //Manga Dex Ids
        mutableUiState
            .filter { state ->
                state.details != null
            }
            .distinctUntilChanged { old, new ->
                old.mediaId == new.mediaId && !new.mangaDexUiState.isRefreshing
            }
            .flatMapLatest { state ->
                getMangaDexUseCase(
                    title = state.details!!.title,
                    nativeTitle = state.details.native,
                    malId = state.details.malId
                )
            }.onEach { result ->
                mutableUiState.update { state ->
                    when(result) {
                        is DataResult.Loading -> {
                            state.copy(
                                mangaDexUiState = state.mangaDexUiState.copy(
                                    isLoading = true,
                                    errorMessage = null,
                                    isRefreshing = false
                                )
                            )
                        }
                        is DataResult.Success -> {
                            state.copy(
                                mangaDexUiState = state.mangaDexUiState.copy(
                                    mangaDexIds = result.data,
                                    isLoading = false
                                )
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                mangaDexUiState = state.mangaDexUiState.copy(
                                    errorMessage = result.message,
                                    isLoading = false
                                )
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
                    progressVolumes = saveUserRate.progressVolumes,
                    repeat = saveUserRate.repeat,
                    status = saveUserRate.userStatus,
                    mediaType = MediaType.MANGA
                )

                mediaTracksRepository.updateMediaTrack(
                    mediaTrack = result.toMediaEntity(),
                    mediaShortData = if(saveUserRate.rateId != null ) null else mediaShortData
                )

                mutableUiState.update { state ->
                    state.copy(
                        details = state.details?.copy(
                            userRate = result
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("MangaDetailsViewModel", "Error creating user rate: ${e.message}")
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