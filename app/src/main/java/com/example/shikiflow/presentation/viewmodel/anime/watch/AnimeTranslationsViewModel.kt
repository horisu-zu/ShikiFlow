package com.example.shikiflow.presentation.viewmodel.anime.watch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.kodik.KodikAnime
import com.example.shikiflow.domain.repository.KodikRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnimeTranslationsViewModel @Inject constructor(
    private val kodikRepository: KodikRepository
): ViewModel() {

    private var currentId: String? = null

    private val _translations = MutableStateFlow<Resource<List<KodikAnime>>>(Resource.Loading())
    val translations = _translations.asStateFlow()

    fun getAnimeTranslation(id: String) {
        viewModelScope.launch {
            try {
                if(currentId == id) return@launch

                _translations.value = Resource.Loading()
                val response = kodikRepository.getAnimeTranslations(id)
                val sortedResponse = response.sortedWith(
                    compareByDescending<KodikAnime> { it.episodesCount }
                        .thenByDescending { it.updatedAt }
                )

                Log.d("AnimeDetailsViewModel", "Translations: $response")
                currentId = id
                _translations.value = Resource.Success(sortedResponse)
            } catch (e: Exception) {
                Log.d("AnimeDetailsViewModel", "Result: ${e.message}")
                _translations.value = Resource.Error(e.message ?: "Unknown error")
            }
        }
    }
}