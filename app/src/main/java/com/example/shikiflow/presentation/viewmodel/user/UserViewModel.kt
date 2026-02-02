package com.example.shikiflow.presentation.viewmodel.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val userFlow = settingsRepository.userFlow
    val authTypeFlow = settingsRepository.authTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = AuthType.SHIKIMORI
        )

    fun fetchCurrentUser() {
        viewModelScope.launch {
            userFlow.collect { localUserData ->
                val result = userRepository.fetchCurrentUser()

                result?.let { currentUser ->
                    if(currentUser != localUserData) {
                        settingsRepository.saveUserData(currentUser)
                    }
                }
            }
        }
    }
}