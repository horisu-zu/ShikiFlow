package com.example.shikiflow.utils

import android.util.Log
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.domain.repository.GithubRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VersionListener @Inject constructor(
    private val githubRepository: GithubRepository
) {
    val owner = BuildConfig.OWNER
    val repo = BuildConfig.REPO

    suspend fun getReleaseByVersion(versionTag: String): GithubRelease? {
        return try {
            Log.d("VersionListener", "Version Tag: $versionTag")
            githubRepository.getReleaseByVersion(owner, repo, versionTag)
        } catch (e: Exception) {
            Log.e("VersionListener", "Failed to get release by version", e)
            null
        }
    }

    suspend fun getLatestRelease(): GithubRelease? {
        return try {
            githubRepository.getLatestRelease(owner, repo)
        } catch (e: Exception) {
            Log.e("VersionListener", "Failed to check for updates", e)
            null
        }
    }
}