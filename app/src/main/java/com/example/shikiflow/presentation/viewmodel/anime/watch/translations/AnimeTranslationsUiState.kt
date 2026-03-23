package com.example.shikiflow.presentation.viewmodel.anime.watch.translations

import com.example.shikiflow.domain.model.kodik.KodikAnime
import com.example.shikiflow.presentation.UiState
import com.example.shikiflow.presentation.screen.main.details.anime.watch.TranslationFilter

data class AnimeTranslationsUiState(
    val animeId: Int? = null,
    val translations: Map<TranslationFilter, List<KodikAnime>> = emptyMap(),

    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    override val errorMessage: String? = null
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}