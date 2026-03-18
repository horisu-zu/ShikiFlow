package com.example.shikiflow.presentation.viewmodel.user

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.UiState
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ProfileUiState(
    val userId: Int? = null,
    val userStatsCategories: UserStatsCategories = UserStatsCategories(),

    override val errorMessage: String? = null,
    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
) : UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository
) : UiStateViewModel<ProfileUiState>() {

    override val initialState: ProfileUiState = ProfileUiState()

    init {
        mutableUiState
            .filter { state ->
                state.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.userId == new.userId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                userRepository.getUserStatsCategories(state.userId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    if(result is DataResult.Success) {
                        state.copy(
                            isLoading = false,
                            errorMessage = null,
                            userStatsCategories = result.data
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }.launchIn(viewModelScope)
    }

    fun setUserId(userId: Int) {
        mutableUiState.update { state ->
            state.copy(userId = userId)
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(
                isRefreshing = true
            )
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