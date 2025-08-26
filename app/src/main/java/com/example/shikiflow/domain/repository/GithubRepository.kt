package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.data.remote.GithubApi
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