package com.example.shikiflow.data.repository

import com.example.shikiflow.data.remote.GithubApi
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.domain.repository.GithubRepository
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val githubApi: GithubApi
): GithubRepository {
    override suspend fun getLatestRelease(
        owner: String,
        repo: String
    ): Result<GithubRelease?> = runCatching { githubApi.getLatestRelease(owner, repo) }

    override suspend fun getReleaseByVersion(
        owner: String,
        repo: String,
        versionTag: String
    ): Result<GithubRelease?> = runCatching {
        githubApi.getReleaseByVersion(owner, repo, versionTag)
    }
}