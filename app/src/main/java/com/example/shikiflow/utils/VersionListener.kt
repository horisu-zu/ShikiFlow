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
            val result = githubRepository.getReleaseByVersion(owner, repo, versionTag)

            if(result.isSuccess) {
                return result.getOrNull()
            } else {
                Log.e("VersionListener", "Failed to get release by version: ${result.exceptionOrNull()}")
                null
            }
        } catch (e: Exception) {
            Log.e("VersionListener", "Failed to get release by version", e)
            null
        }
    }

    fun isUpdateAvailable(currentVersion: String, remoteVersion: String): Boolean {
        val current = currentVersion.split(".").map { it.toIntOrNull() ?: 0 }
        val remote = remoteVersion.split(".").map { it.toIntOrNull() ?: 0 }
        Log.d("VersionListener", "Current Version: $currentVersion, Remote Version: $remoteVersion")

        for (i in 0 until minOf(current.size, remote.size)) {
            if (remote[i] > current[i]) return true
            if (remote[i] < current[i]) return false
        }

        return remote.size > current.size
    }

    suspend fun getLatestRelease(): GithubRelease? {
        return try {
            val result = githubRepository.getLatestRelease(owner, repo)

            if(result.isSuccess) {
                return result.getOrNull()
            } else {
                Log.e("VersionListener", "Failed to check for updates: ${result.exceptionOrNull()}")
                null
            }
        } catch (e: Exception) {
            Log.e("VersionListener", "Failed to check for updates", e)
            null
        }
    }
}