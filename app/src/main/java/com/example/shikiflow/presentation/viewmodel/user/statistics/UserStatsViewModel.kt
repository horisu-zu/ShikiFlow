package com.example.shikiflow.presentation.viewmodel.user.statistics

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.UserStatsSectionType
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.collections.set

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserStatsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : UiStateViewModel<UserStatsUiState>(), UserStatsEvent {

    override val initialState: UserStatsUiState = UserStatsUiState()

    init {
        //Overview
        mutableUiState
            .filter { uiState ->
                uiState.statsSectionType == UserStatsSectionType.OVERVIEW &&
                        uiState.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.userId == new.userId && !new.isRefreshing
            }
            .flatMapLatest { state ->
                userRepository.getUserRates(state.userId!!)
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
                                overviewStats = result.data,
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

        //Genres
        mutableUiState
            .filter { uiState ->
                uiState.statsSectionType == UserStatsSectionType.GENRES &&
                        uiState.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.userId == new.userId && !new.genreStats.isRefreshing
            }
            .flatMapLatest { state ->
                userRepository.getUserGenres(state.userId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    state.copy(
                        genreStats = when(result) {
                            is DataResult.Loading -> {
                                state.genreStats.copy(
                                    isLoading = true,
                                    errorMessage = null,
                                    isRefreshing = false
                                )
                            }
                            is DataResult.Success -> {
                                state.genreStats.copy(
                                    stats = result.data,
                                    isLoading = false
                                )
                            }
                            is DataResult.Error -> {
                                state.genreStats.copy(
                                    errorMessage = result.message,
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
            }.launchIn(viewModelScope)

        //Tags
        mutableUiState
            .filter { uiState ->
                uiState.statsSectionType == UserStatsSectionType.TAGS &&
                        uiState.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.userId == new.userId && !new.tagsStats.isRefreshing
            }
            .flatMapLatest { state ->
                userRepository.getUserTags(state.userId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    state.copy(
                        tagsStats = when(result) {
                            is DataResult.Loading -> {
                                state.tagsStats.copy(
                                    isLoading = true,
                                    errorMessage = null,
                                    isRefreshing = false
                                )
                            }
                            is DataResult.Success -> {
                                state.tagsStats.copy(
                                    stats = result.data,
                                    isLoading = false
                                )
                            }
                            is DataResult.Error -> {
                                state.tagsStats.copy(
                                    errorMessage = result.message,
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
            }.launchIn(viewModelScope)

        //Staff
        mutableUiState
            .filter { uiState ->
                uiState.statsSectionType == UserStatsSectionType.STAFF &&
                        uiState.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.userId == new.userId && !new.staffStats.isRefreshing
            }
            .flatMapLatest { state ->
                userRepository.getUserStaff(state.userId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    state.copy(
                        staffStats = when(result) {
                            is DataResult.Loading -> {
                                state.staffStats.copy(
                                    isLoading = true,
                                    errorMessage = null,
                                    isRefreshing = false
                                )
                            }
                            is DataResult.Success -> {
                                state.staffStats.copy(
                                    stats = result.data,
                                    isLoading = false
                                )
                            }
                            is DataResult.Error -> {
                                state.staffStats.copy(
                                    errorMessage = result.message,
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
            }.launchIn(viewModelScope)

        //Voice Actors
        mutableUiState
            .filter { uiState ->
                uiState.statsSectionType == UserStatsSectionType.VOICE_ACTORS &&
                        uiState.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.userId == new.userId && !new.voiceActorsStats.isRefreshing
            }
            .flatMapLatest { state ->
                userRepository.getUserVoiceActors(state.userId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    state.copy(
                        voiceActorsStats = when(result) {
                            is DataResult.Loading -> {
                                state.voiceActorsStats.copy(
                                    isLoading = true,
                                    errorMessage = null,
                                    isRefreshing = false
                                )
                            }
                            is DataResult.Success -> {
                                state.voiceActorsStats.copy(
                                    stats = result.data,
                                    isLoading = false
                                )
                            }
                            is DataResult.Error -> {
                                state.voiceActorsStats.copy(
                                    errorMessage = result.message,
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
            }.launchIn(viewModelScope)

        //Studios
        mutableUiState
            .filter { uiState ->
                uiState.statsSectionType == UserStatsSectionType.STUDIOS &&
                        uiState.userId != null
            }
            .distinctUntilChanged { old, new ->
                old.userId == new.userId && !new.studiosStats.isRefreshing
            }
            .flatMapLatest { state ->
                userRepository.getUserStudios(state.userId!!)
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    state.copy(
                        studiosStats = when(result) {
                            is DataResult.Loading -> {
                                state.studiosStats.copy(
                                    isLoading = true,
                                    errorMessage = null,
                                    isRefreshing = false
                                )
                            }
                            is DataResult.Success -> {
                                state.studiosStats.copy(
                                    stats = result.data,
                                    isLoading = false
                                )
                            }
                            is DataResult.Error -> {
                                state.studiosStats.copy(
                                    errorMessage = result.message,
                                    isLoading = false
                                )
                            }
                        }
                    )
                }
            }.launchIn(viewModelScope)

        settingsRepository.authTypeFlow
            .filterNotNull()
            .distinctUntilChanged()
            .onEach { authType ->
                mutableUiState.update { state ->
                    state.copy(
                        authType = authType,
                        statsSectionType = UserStatsSectionType.OVERVIEW
                    )
                }
            }.launchIn(viewModelScope)
    }

    override fun setInitialParams(userId: Int, typesList: List<MediaType>) {
        mutableUiState.update { state ->
            state.copy(
                userId = userId,
                typesList = typesList
            )
        }
    }

    override fun setMediaType(mediaType: MediaType) {
        mutableUiState.update { state ->
            state.copy(mediaType = mediaType)
        }
    }

    override fun setStatsSectionType(statsSectionType: UserStatsSectionType) {
        mutableUiState.update { state ->
            state.copy(statsSectionType = statsSectionType)
        }
    }

    override fun setScoreBarType(scoreBarType: StatsBarType) {
        mutableUiState.update { state ->
            state.copy(
                scoreBarType = state.scoreBarType.toMutableMap().apply {
                    this[state.mediaType] = scoreBarType
                }
            )
        }
    }

    override fun setLengthBarType(
        lengthBarType: StatsBarType
    ) {
        mutableUiState.update { state ->
            state.copy(
                lengthBarType = state.lengthBarType.toMutableMap().apply {
                    this[state.mediaType] = lengthBarType
                }
            )
        }
    }

    override fun setReleaseYearBarType(
        releaseYearBarType: StatsBarType
    ) {
        mutableUiState.update { state ->
            state.copy(
                releaseYearBarType = state.releaseYearBarType.toMutableMap().apply {
                    this[state.mediaType] = releaseYearBarType
                }
            )
        }
    }

    override fun setStartYearBarType(
        startYearBarType: StatsBarType
    ) {
        mutableUiState.update { state ->
            state.copy(
                startYearBarType = state.startYearBarType.toMutableMap().apply {
                    this[state.mediaType] = startYearBarType
                }
            )
        }
    }

    override fun setGenresBarType(
        genresBarType: StatsBarType
    ) {
        mutableUiState.update { state ->
            state.copy(
                genresBarType = state.genresBarType.toMutableMap().apply {
                    this[state.mediaType] = genresBarType
                }
            )
        }
    }

    override fun setTagsBarType(tagsBarType: StatsBarType) {
        mutableUiState.update { state ->
            state.copy(
                tagsBarType = state.tagsBarType.toMutableMap().apply {
                    this[state.mediaType] = tagsBarType
                }
            )
        }
    }

    override fun setStaffBarType(staffBarType: StatsBarType) {
        mutableUiState.update { state ->
            state.copy(
                staffBarType = state.staffBarType.toMutableMap().apply {
                    this[state.mediaType] = staffBarType
                }
            )
        }
    }

    override fun setVoiceActorsBarType(voiceActorsBarType: StatsBarType) {
        mutableUiState.update { state ->
            state.copy(
                voiceActorsBarType = voiceActorsBarType
            )
        }
    }

    override fun setStudiosBarType(studiosBarType: StatsBarType) {
        mutableUiState.update { state ->
            state.copy(
                studiosBarType = studiosBarType
            )
        }
    }

    override fun onRefresh(sectionType: UserStatsSectionType) {
        mutableUiState.update { state ->
            when (sectionType) {
                UserStatsSectionType.OVERVIEW -> state.copy(isRefreshing = true)
                UserStatsSectionType.GENRES -> state.copy(
                    genreStats = state.genreStats.copy(isRefreshing = true)
                )
                UserStatsSectionType.TAGS -> state.copy(
                    tagsStats = state.tagsStats.copy(isRefreshing = true)
                )
                UserStatsSectionType.STAFF -> state.copy(
                    staffStats = state.staffStats.copy(isRefreshing = true)
                )
                UserStatsSectionType.VOICE_ACTORS -> state.copy(
                    voiceActorsStats = state.voiceActorsStats.copy(isRefreshing = true)
                )
                UserStatsSectionType.STUDIOS -> state.copy(
                    studiosStats = state.studiosStats.copy(isRefreshing = true)
                )
            }
        }
    }
}