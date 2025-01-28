package com.example.shikiflow.presentation.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.user.UserHistoryResponse
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserHistoryViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _userHistoryData = MutableStateFlow<List<UserHistoryResponse?>>(emptyList())
    val userHistoryData = _userHistoryData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages = _hasMorePages.asStateFlow()

    private var currentPage = 1

    fun loadUserHistory(
        userId: Long,
        isRefresh: Boolean = false
    ) {
        if (_isLoading.value) return

        viewModelScope.launch {
            _isLoading.value = true

            if (isRefresh) {
                currentPage = 1
                _userHistoryData.value = emptyList()
                _hasMorePages.value = true
            }

            try {
                val result = userRepository.getUserHistory(
                    userId = userId,
                    page = currentPage,
                    limit = 20
                )

                result.onSuccess { response ->
                    val combinedHistory = if (isRefresh) {
                        response
                    } else {
                        _userHistoryData.value + response
                    }

                    _userHistoryData.value = combinedHistory
                    _hasMorePages.value = response.size >= 20
                    Log.d("UserHistoryViewModel", "hasMorePages: ${_hasMorePages.value}")

                    if (response.isNotEmpty()) {
                        currentPage++
                    }
                }.onFailure {
                    Log.e("UserHistoryViewModel", "error: $it")
                    _hasMorePages.value = false
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}