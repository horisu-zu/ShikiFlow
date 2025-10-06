package com.example.shikiflow.presentation.viewmodel.anime.watch

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.kodik.KodikAnime
import com.example.shikiflow.domain.usecase.GetAnimeTranslationsUseCase
import com.example.shikiflow.presentation.screen.main.details.anime.watch.TranslationFilter
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AnimeTranslationsViewModel @Inject constructor(
    private val getAnimeTranslationsUseCase: GetAnimeTranslationsUseCase
): ViewModel() {

    private var currentId: String? = null
    private val _translations = MutableStateFlow<Resource<Map<TranslationFilter, List<KodikAnime>>>>(Resource.Loading())
    val translations = _translations.asStateFlow()

    var isRefreshing = mutableStateOf(false)
        private set

    fun getAnimeTranslations(id: String, isRefresh: Boolean = false) {
        if(currentId == id && !isRefresh) {
            return
        } else if(isRefresh) { isRefreshing.value = true }

        getAnimeTranslationsUseCase(id).onEach { result ->
            _translations.value = result
            when(result) {
                is Resource.Loading -> {
                    Log.d("AnimeTranslationsViewModel","Loading translations with ID: $id")
                }
                is Resource.Success -> {
                    currentId = id
                    if(isRefreshing.value) {
                        isRefreshing.value = false
                    }
                    Log.d("AnimeTranslationsViewModel","Result: ${result.data}")
                }
                is Resource.Error -> {
                    Log.d("AnimeTranslationsViewModel", "Error: ${result.message}")
                }
            }
        }.launchIn(viewModelScope)
    }
}