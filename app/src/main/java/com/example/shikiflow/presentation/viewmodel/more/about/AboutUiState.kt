package com.example.shikiflow.presentation.viewmodel.more.about

import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.presentation.UiState
import kotlin.time.Instant

data class AboutUiState(
    val latestRelease: GithubRelease? = null,
    val currentRelease: GithubRelease = GithubRelease(
        tagName = BuildConfig.VERSION_NAME,
        publishedAt = Instant.fromEpochMilliseconds(BuildConfig.VERSION_TIMESTAMP)
    ),

    override val isLoading: Boolean = false,
    override val errorMessage: String? = null,
) : UiState() {
    override fun setError(value: String?) = copy(errorMessage = value)
    override fun setLoading(value: Boolean) = copy(isLoading = value)
}