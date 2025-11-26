package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.common.GithubRelease

interface GithubRepository {
    suspend fun getLatestRelease(
        owner: String,
        repo: String
    ): GithubRelease

    suspend fun getReleaseByVersion(
        owner: String,
        repo: String,
        versionTag: String
    ): GithubRelease?

    /*suspend fun getLatestLocalVersion(
        versionTag: String
    ): GithubRelease?

    suspend fun saveLocalVersion(
        githubRelease: GithubRelease
    )*/
}