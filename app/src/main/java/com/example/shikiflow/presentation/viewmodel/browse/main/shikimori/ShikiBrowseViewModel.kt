package com.example.shikiflow.presentation.viewmodel.browse.main.shikimori

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.settings.BrowseUiMode
import com.example.shikiflow.domain.model.settings.BrowseUiSettings
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.utils.result.PagedResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private data class BrowseRequest(
    val mediaType: MediaType,
    val page: Int,
    val hasNextPage: Boolean,
    val sort: MediaSort,
    val isRefreshing: Boolean
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ShikiBrowseViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel(), ShikiBrowseEvent {
    private val _uiState = MutableStateFlow(ShikiBrowseUiState())
    val uiState = _uiState.asStateFlow()

    val browseUiSettings = settingsRepository.browseUiSettingsFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = BrowseUiSettings()
        )

    init {
        browseUiSettings
            .mapNotNull { settings ->
                settings.browseOngoingOrder
            }
            .distinctUntilChanged()
            .onEach { mediaSort ->
                _uiState.update { state ->
                    state.copy(
                        mainSections = state.mainSections.mapValues { (_, section) ->
                            section.entries.clear()

                            section.copy(
                                page = 1,
                                sort = mediaSort
                            )
                        }
                    )
                }
            }.launchIn(viewModelScope)

        _uiState
            .mapNotNull { state ->
                val section = state.mainSections[state.mediaType] ?: return@mapNotNull null
                val sort = section.sort ?: return@mapNotNull null

                BrowseRequest(state.mediaType, section.page, section.hasNextPage, sort, section.isRefreshing)
            }
            .filter { state -> state.hasNextPage }
            .distinctUntilChanged { old, new ->
                old.mediaType == new.mediaType && old.page == new.page &&
                old.sort == new.sort && !new.isRefreshing
            }
            .flatMapLatest { request ->
                mediaRepository.pagedBrowseMedia(
                    page = request.page,
                    browseOptions = MediaBrowseOptions(
                        mediaType = request.mediaType,
                        status = MediaStatus.ONGOING,
                        sort = Sort(
                            type = request.sort,
                            direction = SortDirection.DESCENDING
                        )
                    ),
                    isRefreshing = request.isRefreshing
                )
            }
            .onEach { result ->
                updateSection { section ->
                    when (result) {
                        is PagedResult.Loading -> {
                            section.copy(
                                isLoading = true,
                                errorMessage = null,
                                isRefreshing = false,
                                hasNextPage = false
                            )
                        }
                        is PagedResult.Success -> {
                            section.entries.addAll(result.list)

                            section.copy(
                                isLoading = false,
                                hasNextPage = result.hasNextPage
                            )
                        }
                        is PagedResult.Error -> {
                            section.copy(
                                isLoading = false,
                                errorMessage = result.message,
                                isRefreshing = false,
                                hasNextPage = false
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    override fun loadMore() {
        updateSection { sectionState ->
            sectionState.setPage(sectionState.page + 1)
        }
    }

    override fun refresh() {
        updateSection { sectionState ->
            sectionState.entries.clear()

            sectionState.copy(
                page = 1,
                isRefreshing = true,
                hasNextPage = true
            )
        }
    }

    override fun retry() {
        updateSection { sectionState ->
            sectionState.copy(
                page = sectionState.page,
                isRefreshing = true,
                hasNextPage = true
            )
        }
    }

    private fun updateSection(
        transform: (ShikiBrowseMediaUiState) -> ShikiBrowseMediaUiState
    ) = _uiState.update { state ->
        val section = state.mainSections[state.mediaType] ?: return@update state

        state.copy(mainSections = state.mainSections + (state.mediaType to transform(section)))
    }

    override fun setMediaType(mediaType: MediaType) {
        _uiState.update { state ->
            state.copy(
                mediaType = mediaType
            )
        }
    }

    override fun setBrowseUiMode(mode: BrowseUiMode) {
        viewModelScope.launch {
            settingsRepository.saveBrowseUiMode(mode)
        }
    }

    override fun setBrowseOngoingOrder(order: MediaSort) {
        viewModelScope.launch {
            settingsRepository.saveBrowseOngoingOrder(order)
        }
    }
}