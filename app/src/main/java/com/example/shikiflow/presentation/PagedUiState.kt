package com.example.shikiflow.presentation

abstract class PagedUiState : UiState() {
    abstract val page: Int
    abstract val hasNextPage: Boolean

    abstract fun setPage(value: Int): PagedUiState
    abstract fun setHasNextPage(value: Boolean): PagedUiState
}