package com.example.shikiflow.presentation.viewmodel.anime.studio

import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.repository.MediaRepository
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class StudioViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val userRepository: UserRepository,
    settingsRepository: SettingsRepository
): UiStateViewModel<StudioUiState>() {

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    override val initialState: StudioUiState = StudioUiState()

    val authType = settingsRepository.authTypeFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = null
        )

    init {
        authType
            .filterNotNull()
            .onEach { authType ->
                setSortType(
                    sortType = when(authType) {
                        AuthType.SHIKIMORI -> MediaSort.Common.SCORE
                        AuthType.ANILIST -> MediaSort.Common.POPULARITY
                    }
                )
            }.launchIn(viewModelScope)

        mutableUiState
            .filter { state ->
                state.studioId != null
            }
            .distinctUntilChanged { old, new ->
                old.studioId == new.studioId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                mediaRepository.getStudio(state.studioId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    when(result) {
                        is DataResult.Loading -> {
                            state.copy(
                                isLoading = true,
                                errorMessage = null,
                                isRefreshing = false
                            )
                        }
                        is DataResult.Success -> {
                            state.copy(
                                studio = result.data,
                                isLoading = false
                            )
                        }
                        is DataResult.Error -> {
                            state.copy(
                                errorMessage = result.message,
                                isLoading = false
                            )
                        }
                    }
                }
            }.launchIn(viewModelScope)
    }

    val studioTitles = combine(
        mutableUiState.filter { state ->
            state.studioId != null && state.sortType != null
        },
        _query.debounce(300L)
    ) { state, query ->
        state.copy(query = query)
    }
        .distinctUntilChanged { old, new ->
            old.studioId == new.studioId && old.sortType == new.sortType &&
            old.query == new.query && old.onUserList == new.onUserList
        }
        .flatMapLatest { state ->
            mediaRepository.getStudioMedia(
                studioId = state.studioId!!,
                search = state.query,
                order = state.sortType,
                onList = state.onUserList
            )
        }.cachedIn(viewModelScope)

    fun toggleFavorite(id: Int) {
        viewModelScope.launch {
            userRepository.toggleFavorite(studioId = id).let { result ->
                if(result is DataResult.Success) {
                    mutableUiState.update { state ->
                        state.copy(
                            studio = state.studio?.copy(
                                isFavorite = !state.studio.isFavorite!!,
                                favorites = when(state.studio.isFavorite) {
                                    true -> state.studio.favorites?.minus(1)
                                    false -> state.studio.favorites?.plus(1)
                                }
                            )
                        )
                    }
                }
            }
        }
    }

    fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(isRefreshing = true)
        }
    }

    fun setStudioId(studioId: Int) {
        mutableUiState.update { state ->
            state.copy(
                studioId = studioId
            )
        }
    }

    fun onUserListSearchChange(value: Boolean?) {
        mutableUiState.update { state ->
            state.copy(
                onUserList = value
            )
        }
    }

    fun setQuery(query: String) {
        _query.update { query }
    }

    fun setSortType(sortType: SortType) {
        mutableUiState.update { state ->
            state.copy(
                sortType = sortType
            )
        }
    }
}