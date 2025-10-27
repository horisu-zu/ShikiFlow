package com.example.shikiflow.presentation.viewmodel.manga.read

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.ChapterMetadata
import com.example.shikiflow.domain.usecase.GetChapterDataUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangaChapterTranslationViewModel @Inject constructor(
    private val getChapterDataUseCase: GetChapterDataUseCase
): ViewModel() {

    private var firstChapterId: String? = null
    private val _chapterTranslations = MutableStateFlow<Resource<List<ChapterMetadata>>>(Resource.Loading())
    val chapterTranslations = _chapterTranslations.asStateFlow()

    fun getChapterTranslations(chapterIds: List<String>) {
        viewModelScope.launch {
            if(firstChapterId == chapterIds.first()) return@launch

            getChapterDataUseCase(chapterIds).collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _chapterTranslations.value = Resource.Loading()
                    }
                    is Resource.Success -> {
                        Log.d("MangaChapterTranslationViewModel", "Chapter translations fetched successfully: ${result.data}")
                        firstChapterId = chapterIds.first()
                        _chapterTranslations.value = result
                    }
                    is Resource.Error -> {
                        Log.e("MangaChapterTranslationViewModel", "Error fetching chapter translations: ${result.message}")
                        _chapterTranslations.value = Resource.Error(result.message ?: "An error occurred")
                    }
                }
            }
        }
    }
}