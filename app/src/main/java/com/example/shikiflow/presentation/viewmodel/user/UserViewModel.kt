package com.example.shikiflow.presentation.viewmodel.user

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
): ViewModel() {

    private val _currentUserData = MutableStateFlow<CurrentUserQuery.Data?>(null)
    val currentUserData: StateFlow<CurrentUserQuery.Data?> = _currentUserData.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchCurrentUser()
    }

    fun fetchCurrentUser() {
        viewModelScope.launch {
            val result = userRepository.fetchCurrentUser()
            if (result.isSuccess) {
                _currentUserData.value = result.getOrNull()
                Log.d("UserViewModel", "User fetched: ${result.getOrNull()?.currentUser?.nickname}")
            } else if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message
                Log.e("UserViewModel", "Error: ${result.exceptionOrNull()?.message}")
            }
        }
    }
}