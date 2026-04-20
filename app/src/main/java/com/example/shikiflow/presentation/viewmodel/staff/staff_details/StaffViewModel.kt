package com.example.shikiflow.presentation.viewmodel.staff.staff_details

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.StaffRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StaffViewModel @Inject constructor(
    private val staffRepository: StaffRepository,
    private val userRepository: UserRepository,
    settingsRepository: SettingsRepository
) : UiStateViewModel<StaffUiState>() {

    override val initialState: StaffUiState = StaffUiState()

    init {
        mutableUiState
            .filter { state ->
                state.staffId != null
            }
            .distinctUntilChanged { old, new ->
                old.staffId == new.staffId && !new.isRefreshing
            }
            .flatMapLatest { uiState ->
                staffRepository.getStaffDetails(uiState.staffId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when (result) {
                        is DataResult.Success -> {
                            state.copy(
                                staffDetails = result.data,
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                isLoading = false,
                                errorMessage = result.message
                            )
                        }
                        else -> {
                            state.copy(
                                isLoading = true,
                                isRefreshing = false,
                                errorMessage = null
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)

        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                mutableUiState.update { state ->
                    state.copy(
                        authType = authType
                    )
                }
            }.launchIn(viewModelScope)
    }

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            userRepository.toggleFavorite(staffId = id).let { result ->
                if(result is DataResult.Success) {
                    mutableUiState.update { state ->
                        state.copy(
                            staffDetails = state.staffDetails?.copy(
                                isFavorite = !state.staffDetails.isFavorite!!,
                                favorites = when(state.staffDetails.isFavorite) {
                                    true -> state.staffDetails.favorites?.minus(1)
                                    false -> state.staffDetails.favorites?.plus(1)
                                }
                            )
                        )
                    }
                }
            }
        }
    }

    fun setStaffId(staffId: Int) {
        mutableUiState.update { state ->
            state.copy(
                staffId = staffId
            )
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(
                isRefreshing = true,
                isLoading = true
            )
        }
    }
}