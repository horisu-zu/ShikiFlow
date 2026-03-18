package com.example.shikiflow.presentation.viewmodel.user.statistics

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.MediaTypeStats
import com.example.shikiflow.domain.model.user.OverviewStats
import com.example.shikiflow.domain.repository.SettingsRepository
import com.example.shikiflow.domain.repository.UserRepository
import com.example.shikiflow.presentation.UiState
import com.example.shikiflow.presentation.screen.more.profile.stats.StatsBarType
import com.example.shikiflow.presentation.screen.more.profile.stats.UserStatsSectionType
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.collections.set

data class UserStatsUiState(
    val userId: Int? = null,
    val authType: AuthType? = null,
    val statsSectionType: UserStatsSectionType = UserStatsSectionType.OVERVIEW,
    val overviewStats: MediaTypeStats<OverviewStats> = MediaTypeStats(),
    val scoreBarType: Map<MediaType, StatsBarType> = emptyMap(),
    val lengthBarType: Map<MediaType, StatsBarType> = emptyMap(),

    override val errorMessage: String? = null,
    override val isLoading: Boolean = true,
    val isRefreshing: Boolean = false
): UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class UserStatsViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val settingsRepository: SettingsRepository
) : UiStateViewModel<UserStatsUiState>(), UserStatsEvent {

    override val initialState: UserStatsUiState = UserStatsUiState()

    init {
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
                    if(result is DataResult.Success) {
                        state.copy(
                            overviewStats = result.data,
                            isLoading = false,
                            isRefreshing = false,
                            errorMessage = null
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }.launchIn(viewModelScope)

        mutableUiState
            .filter { uiState ->
                uiState.userId != null
            }
            .distinctUntilChanged()
            .flatMapLatest {
                settingsRepository.authTypeFlow
            }
            .onEach { authType ->
                mutableUiState.update { state ->
                    state.copy(
                        authType = authType
                    )
                }
            }.launchIn(viewModelScope)
    }

    override fun setUserId(userId: Int) {
        mutableUiState.update { state ->
            state.copy(userId = userId)
        }
    }

    override fun setStatsSectionType(statsSectionType: UserStatsSectionType) {
        mutableUiState.update { state ->
            state.copy(statsSectionType = statsSectionType)
        }
    }

    override fun setScoreBarType(mediaType: MediaType, scoreBarType: StatsBarType) {
        mutableUiState.update { state ->
            state.copy(
                scoreBarType = state.scoreBarType.toMutableMap().apply {
                    this[mediaType] = scoreBarType
                }
            )
        }
    }

    override fun setLengthBarType(
        mediaType: MediaType,
        lengthBarType: StatsBarType
    ) {
        mutableUiState.update { state ->
            state.copy(
                lengthBarType = state.lengthBarType.toMutableMap().apply {
                    this[mediaType] = lengthBarType
                }
            )
        }
    }

    override fun onRefresh() {
        mutableUiState.update { state ->
            state.copy(
                isRefreshing = true,
                isLoading = true
            )
        }
    }
}