package com.example.shikiflow.presentation.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.data.tracks.UserRateRequest
import com.example.shikiflow.data.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _currentUserData =
        MutableStateFlow<Resource<CurrentUserQuery.Data?>>(Resource.Loading())
    val currentUserData = _currentUserData.asStateFlow()

    private val _isUpdating = MutableStateFlow(false)
    val isUpdating = _isUpdating.asStateFlow()

    init {
        fetchCurrentUser()
    }

    private fun fetchCurrentUser() {
        viewModelScope.launch {
            val result = userRepository.fetchCurrentUser()
            if (result.isSuccess) {
                _currentUserData.value = Resource.Success(result.getOrNull())
                Log.d("UserViewModel", "User fetched: ${result.getOrNull()?.currentUser?.nickname}")
            } else if (result.isFailure) {
                _currentUserData.value = Resource.Error(
                    result.exceptionOrNull()?.message
                        ?: "Unknown error"
                )
                Log.e("UserViewModel", "Error: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun updateUserRate(
        id: Long,
        status: Int,
        score: Int,
        progress: Int,
        rewatches: Int,
        mediaType: MediaType,
        onComplete: ((Boolean) -> Unit)? = null
    ) = viewModelScope.launch {
        _isUpdating.value = true

        try {
            val request = UserRateRequest(
                status = UserRateStatusConstants.convertToApiStatus(status),
                score = score.takeIf { it > 0 },
                rewatches = rewatches,
                episodes = if (mediaType == MediaType.ANIME) progress else null,
                chapters = if (mediaType == MediaType.MANGA) progress else null
            )

            val result = withContext(Dispatchers.IO) {
                userRepository.updateUserRate(id, request)
            }.isSuccess

            onComplete?.invoke(result)
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error updating user rate", e)
            onComplete?.invoke(false)
        } finally {
            _isUpdating.value = false
        }
    }
}