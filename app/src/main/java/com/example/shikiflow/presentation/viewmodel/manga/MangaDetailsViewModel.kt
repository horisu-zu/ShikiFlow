package com.example.shikiflow.presentation.viewmodel.manga

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.MangaDetailsQuery
import com.example.shikiflow.domain.model.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.model.track.manga.MangaUserTrack.Companion.toEntity
import com.example.shikiflow.domain.model.tracks.CreateUserRateRequest
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.tracks.UserRateRequest
import com.example.shikiflow.domain.repository.MangaRepository
import com.example.shikiflow.domain.repository.MangaTracksRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.domain.usecase.GetMangaDexUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaDetailsViewModel @Inject constructor(
    private val mangaRepository: MangaRepository,
    private val userRepository: UserRepository,
    private val mangaTracksRepository: MangaTracksRepository,
    private val getMangaDexUseCase: GetMangaDexUseCase
): ViewModel() {

    private var currentId: String? = null

    private val _mangaDetails = MutableStateFlow<Resource<MangaDetailsQuery.Manga>>(Resource.Loading())
    val mangaDetails = _mangaDetails.asStateFlow()

    private val _mangaDexIds = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val mangaDexIds = _mangaDexIds.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    private val _updateEvent = MutableSharedFlow<Unit>()
    val updateEvent = _updateEvent.asSharedFlow()

    fun getMangaDetails(id: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if(!isRefresh && currentId != id) {
                _mangaDetails.value = Resource.Loading()
            }

            try {
                val result = mangaRepository.getMangaDetails(id)

                result?.let { mangaDetails ->

                    getMangaDexId(
                        title = mangaDetails.name,
                        malId = mangaDetails.malId ?: ""
                    )

                    _mangaDetails.value = Resource.Success(mangaDetails)
                    currentId = id
                }
            } catch (e: Exception) {
                _mangaDetails.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun getMangaDexId(title: String, malId: String) {
        viewModelScope.launch {
            Log.d("MangaDetailsViewModel", "Fetching MangaDex ID for title: $title, MAL ID: $malId")
            _mangaDexIds.value = getMangaDexUseCase(title, malId)
            when(val result = _mangaDexIds.value) {
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
                _isUpdating.value = true

                val request = UserRateRequest(
                    status = UserRateStatusConstants.convertToApiStatus(status),
                    score = score.takeIf { it > 0 },
                    chapters = progress,
                    rewatches = rewatches
                )

                val result = userRepository.updateUserRate(id, request)

                mangaTracksRepository.updateMangaTrack(result.toEntity())
            } catch (e: Exception) {
                Log.e("MangaDetailsViewModel", "Error updating user rate", e)
            } finally {
                _updateEvent.emit(Unit)
                currentId?.let { id ->
                    getMangaDetails(id, isRefresh = true)
                }
                _isUpdating.value = false
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
                _isUpdating.value = true

                val request = CreateUserRateRequest(
                    userId = userId.toLong(),
                    targetId = targetId.toLong(),
                    status = UserRateStatusConstants.convertToApiStatus(status),
                    targetType = TargetType.MANGA
                )

                val result = userRepository.createUserRate(request)

                mangaTracksRepository.updateMangaTrack(result.toEntity())
            } catch (e: Exception) {
                Log.e("MangaDetailsViewModel", "Error creating user rate: ${e.message}")
            } finally {
                _updateEvent.emit(Unit)
                getMangaDetails(targetId, isRefresh = true)
                _isUpdating.value = false
            }
        }
    }
}