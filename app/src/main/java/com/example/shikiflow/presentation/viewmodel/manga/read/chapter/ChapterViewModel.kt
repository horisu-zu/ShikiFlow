package com.example.shikiflow.presentation.viewmodel.manga.read.chapter

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.settings.MangaChapterSettings
import com.example.shikiflow.domain.repository.MangaDexRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.usecase.LoadChapterUseCase
import com.example.shikiflow.domain.usecase.UpdateMangaProgressUseCase
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class ChapterViewModel @Inject constructor(
    private val loadChapterUseCase: LoadChapterUseCase,
    private val mangaDexRepository: MangaDexRepository,
    private val settingsRepository: SettingsRepository,
    private val updateMangaProgressUseCase: UpdateMangaProgressUseCase
): ViewModel() {

    private val _interactionEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val _isFocused = MutableStateFlow(false)
    private val _chapterUiState = MutableStateFlow(ChapterUiState())
    val chapterUiState = _chapterUiState.asStateFlow()

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
                old.chapterId == new.chapterId && !new.isRefreshing &&
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

        viewModelScope.launch {
            _chapterUiState
                .distinctUntilChangedBy { state ->
                    state.currentPageIndex
                }
                .filter { state ->
                    state.currentPageIndex == state.chapterData.lastIndex
                }
                .collectLatest { state ->
                    val shouldUpdate = settingsRepository.mangaSettingsFlow
                        .map { it.updateTrackProgress }
                        .first()

                    if (!shouldUpdate) return@collectLatest

                    state.chapterNumber?.let { chapterNumber ->
                        updateMangaProgressUseCase(state.malId!!, chapterNumber)
                    }
                }
        }
    }

    val mangaChaptersItems = _chapterUiState
        .filter { state ->
            state.mangaId != null && (state.scanlationGroupsIds != null || state.uploader != null)
        }
        .distinctUntilChangedBy { state ->
            state.mangaId
        }
        .flatMapLatest { state ->
            mangaDexRepository.getGroupMangaChapters(
                mangaId = state.mangaId!!,
                scanlationGroups = state.scanlationGroupsIds!!,
                uploader = if(state.scanlationGroupsIds.isEmpty()) state.uploader else null
            )
        }.cachedIn(viewModelScope)

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

    fun updatePage(pageIndex: Int) {
        _chapterUiState.update { state ->
            state.copy(
                currentPageIndex = pageIndex
            )
        }
    }

    fun setChapterData(
        mangaId: String,
        malId: Int,
        chapterId: String,
        chapterNumber: Double,
        scanlationGroupsIds: List<String>,
        uploader: String?
    ) {
        _chapterUiState.update { state ->
            state.copy(
                mangaId = mangaId,
                malId = malId,
                chapterId = chapterId,
                chapterNumber = chapterNumber,
                scanlationGroupsIds = scanlationGroupsIds,
                uploader = uploader
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