package com.example.shikiflow.presentation.viewmodel.browse.calendar

import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.presentation.UiState
import kotlinx.datetime.LocalDate

data class OngoingsCalendarUiState(
    val ongoings: Map<LocalDate, List<Browse>> = emptyMap(),

    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}
