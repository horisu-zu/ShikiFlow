package com.example.shikiflow.presentation.viewmodel.manga.read.chapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.mangadex.chapter_metadata.MangaChapterMetadata
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.usecase.LoadChapterUseCase
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val loadChapterUseCase: LoadChapterUseCase,
    private val mangaDexRepository: MangaDexRepository,
    private val settingsRepository: SettingsRepository
): ViewModel() {

    private val _interactionEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val _isFocused = MutableStateFlow(false)
    private val _chapterUiState = MutableStateFlow(ChapterUiState())
    val chapterUiState = _chapterUiState.asStateFlow()

    private val _pagingChaptersCache = mutableMapOf<String, Flow<PagingData<MangaChapterMetadata>>>()
    private val hideDelayMs = 2500L

    init {
        settingsRepository.mangaSettingsFlow.onEach { mangaChapterSettings ->
            _chapterUiState.update { state ->
                state.copy(
                    uiSettings = mangaChapterSettings
                )
            }
        }.launchIn(viewModelScope)

        _chapterUiState
            .filter { state ->
                state.chapterId != null
            }
            .distinctUntilChanged { old, new ->
                old.chapterId == new.chapterId &&
                    !new.isRefreshing &&
                    old.uiSettings.isDataSaverEnabled == new.uiSettings.isDataSaverEnabled
            }
            .flatMapLatest { state ->
                loadChapterUseCase(state.chapterId!!, state.uiSettings.isDataSaverEnabled)
            }
            .onEach { result ->
                when(result) {
                    is DataResult.Loading -> {
                        _chapterUiState.update { state ->
                            state.copy(
                                isLoading = true,
                                chapterError = null,
                                isRefreshing = false
                            )
                        }
                    }
                    is DataResult.Success -> {
                        _chapterUiState.update { state ->
                            state.copy(
                                chapterData = result.data,
                                isLoading = false
                            )
                        }
                    }
                    is DataResult.Error -> {
                        _chapterUiState.update { state ->
                            state.copy(
                                chapterError = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)

        _interactionEvent
            .flatMapLatest {
                _isFocused.flatMapLatest { isFocused ->
                    flow {
                        emit(true)
                        if(!isFocused) {
                            delay(hideDelayMs)
                            emit(false)
                        }
                    }
                }
            }
            .onEach { isInteracting ->
                _chapterUiState.update { state ->
                    state.copy(isNavigationVisible = isInteracting)
                }
            }.launchIn(viewModelScope)
    }

    fun getMangaChapters(
        mangaId: String,
        groupIds: List<String>,
        uploader: String?
    ): Flow<PagingData<MangaChapterMetadata>> {
        return _pagingChaptersCache.getOrPut(mangaId) {
            mangaDexRepository.getGroupMangaChapters(
                mangaId = mangaId,
                groupIds = groupIds,
                uploader = if(groupIds.isEmpty()) uploader else null
            ).cachedIn(viewModelScope)
        }
    }

    fun updateSettings(newSettings: MangaChapterSettings) {
        viewModelScope.launch {
            settingsRepository.updateMangaSettings(newSettings)
        }
    }

    fun onInteractionStart() {
        _interactionEvent.tryEmit(Unit)
    }

    fun changeFocusedState(newValue: Boolean) {
        _isFocused.value = newValue
    }

    fun setChapter(chapterId: String) {
        _chapterUiState.update { state ->
            state.copy(
                chapterId = chapterId
            )
        }
    }

    fun onRefresh() {
        _chapterUiState.update { state ->
            state.copy(
                isRefreshing = true
            )
        }
    }
}