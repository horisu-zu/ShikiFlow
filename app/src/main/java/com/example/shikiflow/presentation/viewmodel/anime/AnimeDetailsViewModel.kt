package com.example.shikiflow.presentation.viewmodel.anime

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.AnimeDetailsQuery
import com.example.shikiflow.domain.model.common.RateUpdateState
import com.example.shikiflow.domain.model.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.model.track.anime.AnimeUserTrack.Companion.toEntity
import com.example.shikiflow.domain.model.tracks.CreateUserRateRequest
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.tracks.UserRateRequest
import com.example.shikiflow.domain.model.tracks.UserRateResponse.Companion.toAnimeUserRate
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.AnimeTracksRepository
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
    private val animeRepository: AnimeRepository,
    private val userRepository: UserRepository,
    private val animeTracksRepository: AnimeTracksRepository
) : ViewModel() {

    private var currentId: String? = null

    private val _animeDetails = MutableStateFlow<Resource<AnimeDetailsQuery.Anime>>(Resource.Loading())
    val animeDetails = _animeDetails.asStateFlow()

    var rateUpdateState = mutableStateOf<RateUpdateState>(RateUpdateState.INITIAL)
        private set

    var isRefreshing = mutableStateOf(false)
        private set

    fun getAnimeDetails(id: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh && currentId != id) {
                _animeDetails.value = Resource.Loading()
            } else if(!isRefresh) {
                return@launch
            } else {
                isRefreshing.value = true
            }

            try {
                val result = animeRepository.getAnimeDetails(id)

                result?.let { animeDetails ->
                    _animeDetails.value = Resource.Success(animeDetails)
                    currentId = id
                    if(isRefreshing.value) { isRefreshing.value = false }
                }
            } catch (e: Exception) {
                _animeDetails.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun updateUserRate(
        id: Long,
        status: Int,
        score: Int,
        progress: Int,
        rewatches: Int
    ) {
        viewModelScope.launch {
            try {
                rateUpdateState.value = RateUpdateState.LOADING

                val request = UserRateRequest(
                    status = UserRateStatusConstants.convertToApiStatus(status),
                    score = score.takeIf { it > 0 },
                    episodes = progress,
                    rewatches = rewatches
                )

                val result = userRepository.updateUserRate(id, request)

                animeTracksRepository.updateAnimeTrack(result.toEntity())

                _animeDetails.update { resource ->
                    if(resource is Resource.Success) {
                        Resource.Success(resource.data?.copy(userRate = result.toAnimeUserRate()))
                    } else { resource }
                }
            } catch (e: Exception) {
                Log.e("AnimeDetailsViewModel", "Error updating user rate", e)
            } finally {
                rateUpdateState.value = RateUpdateState.FINISHED
            }
        }
    }

    fun createUserRate(
        userId: String,
        targetId: String,
        status: Int
    ) {
        viewModelScope.launch {
            try {
                rateUpdateState.value = RateUpdateState.LOADING

                val request = CreateUserRateRequest(
                    userId = userId.toLong(),
                    targetId = targetId.toLong(),
                    status = UserRateStatusConstants.convertToApiStatus(status),
                    targetType = TargetType.ANIME
                )

                val result = userRepository.createUserRate(request)

                animeTracksRepository.updateAnimeTrack(result.toEntity())

                _animeDetails.update { resource ->
                    if(resource is Resource.Success) {
                        Resource.Success(resource.data?.copy(userRate = result.toAnimeUserRate()))
                    } else { resource }
                }
            } catch (e: Exception) {
                Log.e("AnimeDetailsViewModel", "Error creating user rate: ${e.message}")
            } finally {
                rateUpdateState.value = RateUpdateState.FINISHED
            }
        }
    }
}