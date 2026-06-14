package com.example.shikiflow.presentation.viewmodel.more.about

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.repository.ReleaseRepository
import com.example.shikiflow.presentation.UiStateViewModel
import com.example.shikiflow.utils.DataResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AboutViewModel @Inject constructor(
    private val releaseRepository: ReleaseRepository
): UiStateViewModel<AboutUiState>(), AboutEvent {

    override val initialState: AboutUiState = AboutUiState()

    init {
        mutableUiState
            .filter { state ->
                state.isLoading
            }
            .distinctUntilChanged()
            .flatMapLatest {
                releaseRepository.getLatestRelease(
                    owner = BuildConfig.OWNER,
                    repo = BuildConfig.REPO
                )
            }
            .onEach { result ->
                mutableUiState.update { state ->
                    if(result is DataResult.Success) {
                        state.copy(
                            latestRelease = result.data,
                            isLoading = false,
                            errorMessage = null
                        )
                    } else {
                        result.toUiState()
                    }
                }
            }.launchIn(viewModelScope)

        mutableUiState
            .filter { state ->
                state.latestRelease != null
            }
            .distinctUntilChanged { old, new ->
                old.latestRelease == new.latestRelease
            }
            .onEach {
                mutableUiState.update { state ->
                    if("v${state.currentRelease.tagName}" == state.latestRelease?.tagName) {
                        state.copy(
                            checkUpdateState = CheckUpdateState.UpToDate
                        )
                    } else {
                        state.copy(
                            checkUpdateState = CheckUpdateState.UpdateAvailable
                        )
                    }
                }
            }.launchIn(viewModelScope)

        mutableUiState
            .filter { state ->
                state.updateState is UpdateState.Completed
            }
            .distinctUntilChangedBy { state -> state.updateState }
            .onEach { state ->
                if(state.updateState is UpdateState.Completed && state.autoInstall) {
                    installRelease(state.updateState.fileName)
                }
            }.launchIn(viewModelScope)
    }

    override fun checkForUpdates() {
        mutableUiState.update { state ->
            state.copy(isLoading = true)
        }
    }

    override fun downloadRelease(fileName: String, url: String) {
        releaseRepository.downloadRelease(url, fileName)
            .onEach { state ->
                mutableUiState.update { uiState ->
                    uiState.copy(
                        updateState = state
                    )
                }
            }.launchIn(viewModelScope)
    }

    override fun installRelease(fileName: String) {
        releaseRepository.installRelease(fileName)
    }

    override fun setAutoInstall(autoInstall: Boolean) {
        mutableUiState.update { state ->
            state.copy(
                autoInstall = autoInstall
            )
        }
    }
}