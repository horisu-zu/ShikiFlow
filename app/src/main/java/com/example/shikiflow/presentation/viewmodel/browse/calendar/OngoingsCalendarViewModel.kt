package com.example.shikiflow.presentation.viewmodel.browse.calendar

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.usecase.GetOngoingsCalendarUseCase
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class OngoingsCalendarViewModel @Inject constructor(
    getOngoingsCalendarUseCase: GetOngoingsCalendarUseCase
): UiStateViewModel<OngoingsCalendarUiState>() {

    override val initialState: OngoingsCalendarUiState = OngoingsCalendarUiState()

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(isRefreshing = true)
        }
    }

    init {
        mutableUiState
            .distinctUntilChanged { _, new ->
                !new.isRefreshing
            }
            .flatMapLatest {
                getOngoingsCalendarUseCase()
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when (result) {
                        is DataResult.Success -> {
                            state.copy(
                                ongoings = result.data,
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
    }
}