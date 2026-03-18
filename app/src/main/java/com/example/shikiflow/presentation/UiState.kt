package com.example.shikiflow.presentation

abstract class UiState {
    abstract val isLoading: Boolean
    abstract val errorMessage: String?

    abstract fun setLoading(value: Boolean): UiState
    abstract fun setError(value: String?): UiState
}