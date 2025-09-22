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
}