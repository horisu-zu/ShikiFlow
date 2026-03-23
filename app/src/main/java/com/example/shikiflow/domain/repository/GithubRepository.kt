package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface GithubRepository {
    suspend fun getLatestRelease(
        owner: String,
        repo: String
    ): Flow<DataResult<GithubRelease>>

    suspend fun getReleaseByVersion(
        owner: String,
        repo: String,
        versionTag: String
    ): Flow<DataResult<GithubRelease>>

    /*suspend fun getLatestLocalVersion(
        versionTag: String
    ): GithubRelease?

    suspend fun saveLocalVersion(
        githubRelease: GithubRelease
    )*/
}