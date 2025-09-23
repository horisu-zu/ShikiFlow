package com.example.shikiflow.presentation.viewmodel.manga.read

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.usecase.DownloadChapterUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val downloadChapterUseCase: DownloadChapterUseCase,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    val mangaSettings: StateFlow<MangaChapterSettings> = settingsRepository.mangaSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = MangaChapterSettings()
        )

    private var currentChapterId = MutableStateFlow<String?>(null)

    val chapterPages: StateFlow<Resource<List<String>>> =
        combine(
            settingsRepository.mangaSettingsFlow,
            currentChapterId
        ) { settings, chapterId ->
            if (chapterId == null) {
                flowOf(Resource.Loading())
            } else {
                downloadChapterUseCase(chapterId, settings.isDataSaverEnabled)
            }
        }.flatMapLatest { it }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = Resource.Loading()
            )

    fun downloadMangaChapter(mangaDexChapterId: String, isRefresh: Boolean = false) {
        if(isRefresh) {
            currentChapterId.value = null
        }
        currentChapterId.value = mangaDexChapterId
    }

    fun updateSettings(newSettings: MangaChapterSettings) {
        viewModelScope.launch {
            settingsRepository.updateMangaSettings(newSettings)
        }
    }
}