package com.example.shikiflow.presentation.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.anime.ShortAnimeRate
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserRateViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {
    private val _userAnimeTrackData = MutableStateFlow<List<ShortAnimeRate?>>(emptyList())
    val userAnimeTrackData = _userAnimeTrackData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)

    private var currentPage = 1

    fun loadUserAnimeRates(
        userId: Long,
        isRefresh: Boolean = false
    ) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            if (isRefresh) {
                currentPage = 1
                _userAnimeTrackData.value = emptyList()
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

                _userAnimeTrackData.value = tempList
            } catch (e: Exception) {
                _hasMorePages.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}