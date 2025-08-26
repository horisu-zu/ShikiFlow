package com.example.shikiflow.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.utils.VersionListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val versionListener: VersionListener
): ViewModel() {
    var latestRelease by mutableStateOf<GithubRelease?>(null)
        private set

    var currentVersion by mutableStateOf<GithubRelease?>(null)
        private set

    init {
        checkForUpdates()
    }

    fun checkForUpdates() {
        val currentAppVersion = BuildConfig.VERSION_NAME

        viewModelScope.launch {
            currentVersion = versionListener.getReleaseByVersion(currentAppVersion)
            val appRelease = versionListener.getLatestRelease()

            appRelease?.let { release ->
                val isUpdateAvailable = versionListener.isUpdateAvailable(
                    currentVersion?.tagName ?: "",
                    release.tagName
                )
                latestRelease = if(isUpdateAvailable) {
                    release
                } else {
                    null
                }
            }
        }
    }
}