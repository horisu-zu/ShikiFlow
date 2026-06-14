package com.example.shikiflow.domain.repository

import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.presentation.viewmodel.more.about.UpdateState
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface ReleaseRepository {
    fun getLatestRelease(
        owner: String,
        repo: String
    ): Flow<DataResult<GithubRelease>>

    fun getReleaseByVersion(
        owner: String,
        repo: String,
        versionTag: String
    ): Flow<DataResult<GithubRelease>>

    fun downloadRelease(url: String, fileName: String): Flow<UpdateState>

    fun installRelease(fileName: String)
}