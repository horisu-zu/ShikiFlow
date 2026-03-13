package com.example.shikiflow.presentation.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.UserRateStats
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserRatesUiState(
    val userMediaStats: UserRateStats = UserRateStats(emptyMap()),
    val favoriteCategories: List<FavoriteCategory> = emptyList(),

    val errorMessage: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class UserRateViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userRatesUiState = MutableStateFlow(UserRatesUiState())
    val userRatesUiState = _userRatesUiState.asStateFlow()

    fun loadUserRates(userId: String) {
        viewModelScope.launch {
            try {
                if(_userRatesUiState.value.userMediaStats.mediaStats.isNotEmpty()) {
                    return@launch
                } else {
                    _userRatesUiState.update { state ->
                        state.copy(isLoading = true)
                    }
                }

                val (userMediaStats, favoriteCategories) = coroutineScope {
                    val mediaStats = async { userRepository.getUserRates(userId.toInt()) }
                    val categories = async { userRepository.getFavoriteCategories(userId.toInt()) }

                    mediaStats.await() to categories.await()
                }

                _userRatesUiState.update { state ->
                    state.copy(
                        userMediaStats = userMediaStats,
                        favoriteCategories = favoriteCategories,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                _userRatesUiState.update { state ->
                    state.copy(errorMessage = e.message)
                }
            } finally {
                _userRatesUiState.update { state ->
                    state.copy(isLoading = false)
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