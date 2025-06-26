package com.example.shikiflow.domain.repository

import com.example.shikiflow.data.common.GithubRelease
import com.example.shikiflow.data.api.GithubApi
import javax.inject.Inject

class GithubRepository @Inject constructor(
    private val githubApi: GithubApi
) {
    suspend fun getLatestRelease(
        owner: String,
        repo: String
    ): Result<GithubRelease?> = runCatching {
        githubApi.getLatestRelease(owner, repo)
    }

    suspend fun getReleaseByVersion(
        owner: String,
        repo: String,
        versionTag: String
    ): Result<GithubRelease?> = runCatching {
        githubApi.getReleaseByVersion(owner, repo, versionTag)
    }
}