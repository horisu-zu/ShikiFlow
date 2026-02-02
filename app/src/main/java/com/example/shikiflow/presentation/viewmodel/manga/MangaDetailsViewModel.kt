package com.example.shikiflow.presentation.viewmodel.manga

import android.util.Log
import androidx.compose.runtime.mutableStateOf
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

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository,
    private val mediaTracksRepository: MediaTracksRepository,
    private val getMangaDexUseCase: GetMangaDexUseCase
): ViewModel() {

    private var currentId: Int? = null

    private val _mangaDetails = MutableStateFlow<Resource<MediaDetails>>(Resource.Loading())
    val mangaDetails = _mangaDetails.asStateFlow()

    private val _mangaDexIds = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val mangaDexIds = _mangaDexIds.asStateFlow()

    var rateUpdateState = mutableStateOf(RateUpdateState.INITIAL)
        private set

    var isRefreshing = mutableStateOf(false)
        private set

    fun getMangaDetails(id: Int, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if(!isRefresh && currentId != id) {
                _mangaDetails.value = Resource.Loading()
            } else if(!isRefresh) {
                return@launch
            } else {
                isRefreshing.value = true
            }

            val result = mediaRepository.getMediaDetails(id, mediaType = MediaType.MANGA)

            result.fold(
                onSuccess = { mediaDetails ->
                    _mangaDetails.value = Resource.Success(mediaDetails)

                    getMangaDexId(
                        title = mediaDetails.title,
                        nativeTitle = mediaDetails.native,
                        malId = mediaDetails.malId
                    )

                    currentId = id
                    if(isRefreshing.value) { isRefreshing.value = false }
                },
                onFailure = { exception ->
                    _mangaDetails.value = Resource.Error(exception.message ?: "Unknown error")
                }
            )
        }
    }

    fun getMangaDexId(title: String, nativeTitle: String?, malId: Int) {
        Log.d("MangaDetailsViewModel", "Fetching MangaDex ID for title: $title, MAL ID: $malId")
        getMangaDexUseCase(title, nativeTitle, malId).onEach { result ->
            _mangaDexIds.value = result

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
                rateUpdateState.value = RateUpdateState.LOADING

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

                _mangaDetails.update { resource ->
                    if (resource is Resource.Success) {
                        Resource.Success(resource.data?.copy(userRate = result))
                    } else { resource }
                }
            } catch (e: Exception) {
                Log.e("MangaDetailsViewModel", "Error creating user rate: ${e.message}")
            } finally {
                rateUpdateState.value = RateUpdateState.FINISHED
            }
        }
    }
}