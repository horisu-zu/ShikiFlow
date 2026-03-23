package com.example.shikiflow.presentation

import androidx.lifecycle.ViewModel
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Suppress("UNCHECKED_CAST")
abstract class UiStateViewModel<S : UiState> : ViewModel() {

    protected abstract val initialState: S
    protected val mutableUiState by lazy { MutableStateFlow(initialState) }
    val uiState: StateFlow<S> by lazy { mutableUiState.asStateFlow() }

    protected fun <D> DataResult<D>.toUiState(): S {
        return when (this) {
            is DataResult.Loading -> mutableUiState.value.setError(null).setLoading(true) as S

            is DataResult.Error -> {
                mutableUiState.value.setError(message).setLoading(false) as S
            }

            is DataResult.Success -> mutableUiState.value.setLoading(false) as S
        }
    }
}