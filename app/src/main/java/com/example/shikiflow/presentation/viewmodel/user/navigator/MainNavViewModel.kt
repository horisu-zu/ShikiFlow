package com.example.shikiflow.presentation.viewmodel.user.navigator

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainNavViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : UiStateViewModel<MainNavUiState>() {

    override val initialState: MainNavUiState = MainNavUiState()

    init {
        viewModelScope.launch {
            settingsRepository.userFlow
                .filterNotNull()
                .onEach { localUserData ->
                    mutableUiState.update { state ->
                        state.copy(user = localUserData, isLoading = true)
                    }
                }
                .flatMapLatest { localUserData ->
                    userRepository.fetchCurrentUser()
                        .map { result -> result to localUserData }
                }
                .collect { (result, localUserData) ->
                    when (result) {
                        is DataResult.Success -> {
                            val currentUser = result.data

                            if (currentUser != localUserData) {
                                settingsRepository.saveUserData(currentUser)
                            }
                            mutableUiState.update { state ->
                                state.copy(isLoading = false)
                            }
                        } else -> {
                            result.toUiState()
                        }
                    }
                }
        }

        viewModelScope.launch {
            settingsRepository.authTypeFlow
                .distinctUntilChanged()
                .collect { authType ->
                    mutableUiState.update { state ->
                        state.copy(authType = authType)
                    }
                }
        }
    }
}