package com.example.shikiflow.presentation.viewmodel.more.about

import androidx.lifecycle.viewModelScope
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.repository.GithubRepository
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

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AboutViewModel @Inject constructor(
    private val githubRepository: GithubRepository
): UiStateViewModel<AboutUiState>() {

    override val initialState: AboutUiState = AboutUiState()

    init {
        mutableUiState
            .filter { state ->
                state.isLoading
            }
            .distinctUntilChanged()
            .flatMapLatest {
                githubRepository.getLatestRelease(
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
    }

    fun checkForUpdates() {
        mutableUiState.update { state ->
            state.copy(isLoading = true)
        }
    }

    /*fun getLocalVersion() {
        viewModelScope.launch {
            if(_currentVersion.value is Resource.Success) return@launch

            try {
                _currentVersion.value = Resource.Loading()

                val currentLocalVersion = githubRepository.getLatestLocalVersion(
                    versionTag = BuildConfig.VERSION_NAME
                )

                val version = currentLocalVersion ?: run {
                    githubRepository.getReleaseByVersion(
                        owner = BuildConfig.OWNER,
                        repo = BuildConfig.REPO,
                        versionTag = BuildConfig.VERSION_NAME
                    ).also { release ->
                        release?.let {
                            githubRepository.saveLocalVersion(release)
                        }
                    }
                }

                _currentVersion.value = Resource.Success(version)
            } catch (e: Exception) {
                Log.e("AboutViewModel", "Error: ${e.message}")
                _currentVersion.value = Resource.Error("Error: ${e.message}")
            }
        }
    }*/
}