package com.example.shikiflow.presentation.viewmodel.user

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.user.UserRateExpanded
import com.example.shikiflow.domain.usecase.GetUserProfileUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRateViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private var _userId: Long? = null

    private val _userRateData = MutableStateFlow<Resource<UserRateExpanded>>(Resource.Loading())
    val userRateData = _userRateData.asStateFlow()

    var isRefreshing = mutableStateOf(false)
        private set

    fun loadUserRates(
        userId: Long,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {
            if (!isRefresh && _userId == userId) {
                return@launch
            } else if(!isRefresh) {
                _userRateData.value = Resource.Loading()
            } else {
                isRefreshing.value = true
            }

            _userRateData.value = getUserProfileUseCase(userId)

            when(val result = _userRateData.value) {
                is Resource.Loading -> { /**/ }
                is Resource.Success -> {
                    _userId = userId
                    Log.d("UserRateViewModel", "Successfully loaded User Rate Data")
                }
                is Resource.Error -> {
                    Log.e("UserRateViewModel", "Error: ${result.message}")
                }
            }
        }
    }

    /*fun loadUserAnimeRates(
        userId: Long,
        isRefresh: Boolean = false
    ) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            if (isRefresh) {
                currentPage = 1
                _userAnimeTrack.value = emptyList()
                _hasMorePages.value = true
            }

            val tempList = mutableListOf<ShortAnimeRate>()

            try {
                while (_hasMorePages.value) {
                    val result = userRepository.getUserAnimeRates(
                        userId = userId,
                        page = currentPage,
                        limit = 200
                    )

                    result.onSuccess { response ->
                        tempList.addAll(response)
                        _hasMorePages.value = response.size >= 200
                        currentPage++
                    }

                    result.onFailure { error ->
                        _hasMorePages.value = false
                    }
                }

                _userAnimeTrack.value = tempList
            } catch (e: Exception) {
                _hasMorePages.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun trackAllData() {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            val tempList = mutableListOf<ShortAnimeTracksQuery.UserRate?>()

            try {
                while (_hasMorePages.value) {
                    val result = animeTracksRepository.getShortAnimeTracks(
                        page = currentPage
                    )

                    result.onSuccess { response ->
                        tempList.addAll(response.userRates)
                        _hasMorePages.value = response.userRates.size >= 50
                        currentPage++
                    }

                    result.onFailure { error ->
                        _hasMorePages.value = false
                    }
                }

                _userAnimeTrackData.value = tempList
            } catch (e: Exception) {
                _hasMorePages.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }*/

    /*fun trackAllData() {
        viewModelScope.launch {
            _isLoading.value = true
            val allTracks = mutableListOf<ShortAnimeTracksQuery.UserRate>()
            var hasNextPage = true
            var currentPage = 1

            try {
                while (hasNextPage) {
                    val pageChunk = (currentPage..minOf(currentPage + 9, 100)).toList()

                    val results = pageChunk.map { page ->
                        async {
                            animeTracksRepository.getShortAnimeTracks(page = page)
                        }
                    }.awaitAll()

                    delay(500)

                    val validResponses = results.filter { it.isSuccess }
                        .mapNotNull { it.getOrNull() }
                        .filter { it.userRates.isNotEmpty() }

                    validResponses.forEach { response ->
                        allTracks.addAll(response.userRates)
                        if (!response.hasNextPage) {
                            hasNextPage = false
                        }
                    }

                    currentPage += 5
                }

                _userAnimeTrackData.value = allTracks

            } catch (e: Exception) {
                // Error Validation
            } finally {
                _isLoading.value = false
            }
        }
    }*/
}