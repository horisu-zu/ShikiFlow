package com.example.shikiflow.presentation.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRateRequest
import com.example.shikiflow.domain.model.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.model.tracks.CreateUserRateRequest
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.tracks.UserRateResponse
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _updateEvent = MutableSharedFlow<UserRateResponse>(0)
    val updateEvent = _updateEvent.asSharedFlow()

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
        mediaType: MediaType
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
            }

            if (result.isSuccess) {
                _updateEvent.emit(result.getOrThrow())
            } else {
                Log.e("UserViewModel", "Error updating user rate")
            }
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error updating user rate", e)
        } finally {
            _isUpdating.value = false
        }
    }

    fun createUserRate(
        userId: String,
        targetId: String,
        status: Int,
        targetType: TargetType
    ) = viewModelScope.launch {
        _isUpdating.value = true

        try {
            val request = CreateUserRateRequest(
                userId = userId.toLong(),
                targetId = targetId.toLong(),
                status = UserRateStatusConstants.convertToApiStatus(status),
                targetType = targetType
            )

            val result = withContext(Dispatchers.IO) {
                userRepository.createUserRate(request)
            }

            if (result.isSuccess) {
                _updateEvent.emit(result.getOrNull()!!)
            } else {
                Log.e("UserViewModel", "Error creating user rate")
            }
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error creating user rate: ${e.message}")
        } finally {
            _isUpdating.value = false
        }
    }
}