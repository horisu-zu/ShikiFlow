package com.example.shikiflow.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.domain.repository.GithubRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val githubRepository: GithubRepository
): ViewModel() {

    private val _latestRelease  = MutableStateFlow<Resource<GithubRelease?>>(Resource.Success(null))
    val latestRelease = _latestRelease.asStateFlow()

    private val _currentVersion = MutableStateFlow<Resource<GithubRelease>>(Resource.Loading())
    val currentVersion = _currentVersion.asStateFlow()

    init {
        getLocalVersion()
    }

    fun checkForUpdates() {
        viewModelScope.launch {
            _latestRelease.value = Resource.Loading()
            runCatching {
                githubRepository.getLatestRelease(
                    owner = BuildConfig.OWNER,
                    repo = BuildConfig.REPO
                )
            }.onSuccess { release ->
                _latestRelease.value = Resource.Success(release)
            }.onFailure { error ->
                _latestRelease.value = Resource.Error("Failed to check for updates: ${error.message}")
            }
        }
    }

    fun getLocalVersion() {
        viewModelScope.launch {
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
    }
}