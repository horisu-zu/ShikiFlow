package com.example.shikiflow.presentation.viewmodel.manga

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.mapper.local.MangaEntityMapper.toMangaEntity
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.track.manga.MangaShortData
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.MediaTracksRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.domain.usecase.GetMangaDexUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MangaDetailsUiState(
    val details: MediaDetails? = null,
    val mangaDexIds: Resource<List<String>> = Resource.Loading(),
    val rateUpdateState: RateUpdateState = RateUpdateState.INITIAL,
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val detailsError: String? = null
)

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository,
    private val mediaTracksRepository: MediaTracksRepository,
    private val getMangaDexUseCase: GetMangaDexUseCase
): ViewModel() {

    private val _details = MutableStateFlow(MangaDetailsUiState())
    val details = _details.asStateFlow()

    fun getMangaDetails(id: Int, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if(_details.value.details == null) {
                _details.update { state ->
                    state.copy(
                        isLoading = true
                    )
                }
            } else if(!isRefresh) {
                return@launch
            } else {
                _details.update { state ->
                    state.copy(
                        isRefreshing = true
                    )
                }
            }

            val result = mediaRepository.getMediaDetails(id, mediaType = MediaType.MANGA)

            result.fold(
                onSuccess = { mediaDetails ->
                    _details.update { state ->
                        state.copy(
                            details = mediaDetails,
                            isLoading = false,
                            isRefreshing = false
                        )
                    }

                    if(_details.value.mangaDexIds !is Resource.Success) {
                        getMangaDexId(
                            title = mediaDetails.title,
                            nativeTitle = mediaDetails.native,
                            malId = mediaDetails.malId
                        )
                    }
                },
                onFailure = { exception ->
                    _details.update { state ->
                        state.copy(
                            detailsError = exception.message ?: "Unknown error"
                        )
                    }
                }
            )
        }
    }

    fun getMangaDexId(title: String, nativeTitle: String?, malId: Int) {
        getMangaDexUseCase(title, nativeTitle, malId).onEach { result ->
            _details.update { state ->
                state.copy(
                    mangaDexIds = result
                )
            }

            when(result) {
                is Resource.Success -> {
                    Log.d("MangaDetailsViewModel", "Successfully fetched MangaDex IDs: ${result.data}")
                }
                is Resource.Error -> {
                    Log.e("MangaDetailsViewModel", "Error fetching MangaDex IDs: ${result.message}")
                }
                is Resource.Loading -> {
                    Log.d("MangaDetailsViewModel", "Loading MangaDex IDs...")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun saveUserRate(
        userId: Int,
        saveUserRate: SaveUserRate,
        mangaShortData: MangaShortData? = null
    ) {
        viewModelScope.launch {
            try {
                _details.update { state ->
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

                mediaTracksRepository.updateMangaTrack(
                    mangaTrack = result.toMangaEntity(),
                    mangaShortData = if(saveUserRate.rateId != null ) null else mangaShortData
                )

                _details.update { state ->
                    state.copy(
                        details = state.details?.copy(
                            userRate = result
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e("MangaDetailsViewModel", "Error creating user rate: ${e.message}")
            } finally {
                _details.update { state ->
                    state.copy(
                        rateUpdateState = RateUpdateState.FINISHED
                    )
                }
            }
        }
    }
}