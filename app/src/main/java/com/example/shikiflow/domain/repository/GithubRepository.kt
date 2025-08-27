package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.common.GithubRelease

interface GithubRepository {
    suspend fun getLatestRelease(
        owner: String,
        repo: String
    ): Result<GithubRelease?>

    suspend fun getReleaseByVersion(
        owner: String,
        repo: String,
        versionTag: String
    ): Result<GithubRelease?>
}