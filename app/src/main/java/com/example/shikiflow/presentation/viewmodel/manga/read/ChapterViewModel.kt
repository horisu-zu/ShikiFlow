package com.example.shikiflow.presentation.viewmodel.manga.read

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.usecase.DownloadChapterUseCase
import com.example.shikiflow.presentation.screen.main.details.manga.read.ChapterUIMode
import com.example.shikiflow.utils.AppSettingsManager
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val downloadChapterUseCase: DownloadChapterUseCase,
    private val appSettingsManager: AppSettingsManager
): ViewModel() {

    val chapterUiMode = appSettingsManager.chapterUiFlow
        .stateIn(viewModelScope, SharingStarted.Lazily, initialValue = ChapterUIMode.SCROLL)

    private val _chapterPages = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val chapterPages = _chapterPages.asStateFlow()

    fun downloadMangaChapter(mangaDexChapterId: String) {
        viewModelScope.launch {
            val isDataSaver = appSettingsManager.dataSaverFlow.first()
            _chapterPages.value = downloadChapterUseCase(mangaDexChapterId, isDataSaver)
            when(val result = _chapterPages.value) {
                is Resource.Loading -> {
                    Log.d("ChapterViewModel", "Loading chapter pages for ID: $mangaDexChapterId")
                }
                is Resource.Success -> {
                    Log.d("ChapterViewModel", "Result: ${result.data}")
                }
                is Resource.Error -> {
                    Log.d("ChapterViewModel", "Error: ${result.message}")
                }
            }
        }
    }
}