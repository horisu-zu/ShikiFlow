package com.example.shikiflow.presentation.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.ShortAnimeTracksQuery
import com.example.shikiflow.data.anime.ShortAnimeRate
import com.example.shikiflow.data.tracks.UserRate
import com.example.shikiflow.domain.repository.AnimeTracksRepository
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeUserRateViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val animeTracksRepository: AnimeTracksRepository
) : ViewModel() {
    private val _userAnimeTrackData =
        MutableStateFlow<List<ShortAnimeTracksQuery.UserRate?>>(emptyList())
    val userAnimeTrackData = _userAnimeTrackData.asStateFlow()

    private val _userRateData = MutableStateFlow<List<UserRate>>(emptyList())
    val userRateData = _userRateData.asStateFlow()

    private val _userAnimeTrack = MutableStateFlow<List<ShortAnimeRate?>>(emptyList())
    val userAnimeTrack = _userAnimeTrack.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)

    fun loadUserRates(
        userId: Long,
        isRefresh: Boolean = false
    ) {
        viewModelScope.launch {

            if (isRefresh) {
                _userRateData.value = emptyList()
                _hasMorePages.value = true
            }

            val tempList = mutableListOf<UserRate>()

            try {
                val result = userRepository.getUserRates(
                    userId = userId
                )

                result.onSuccess { response ->
                    tempList.addAll(response)
                }

                result.onFailure { error ->
                    Log.d("UserRateViewModel", "Error: $error")
                    _hasMorePages.value = false
                }

                Log.d("UserRateViewModel", "UserRateData: $tempList")
                _userRateData.value = tempList
            } catch (e: Exception) {
                _hasMorePages.value = false
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