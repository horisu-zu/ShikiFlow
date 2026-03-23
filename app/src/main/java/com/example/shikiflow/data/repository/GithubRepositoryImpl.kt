package com.example.shikiflow.data.repository

import com.example.shikiflow.data.remote.GithubApi
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.domain.repository.GithubRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val githubApi: GithubApi
): GithubRepository {
    override suspend fun getLatestRelease(
        owner: String,
        repo: String
    ): Flow<DataResult<GithubRelease>> = flow {
        emit(DataResult.Loading)

        try {
            val latestRelease = githubApi.getLatestRelease(owner, repo)

            emit(DataResult.Success(latestRelease))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override suspend fun getReleaseByVersion(
        owner: String,
        repo: String,
        versionTag: String
    ): Flow<DataResult<GithubRelease>> = flow {
        emit(DataResult.Loading)

        try {
            val versionRelease = githubApi.getReleaseByVersion(owner, repo, versionTag)

            versionRelease?.let {
                emit(DataResult.Success(versionRelease))
            } ?: emit(DataResult.Error(message = "Empty Response"))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }

    /*override suspend fun getLatestLocalVersion(versionTag: String): GithubRelease? {
        return versionDao.getLatestLocalVersion(
            localVersionTag = "v$versionTag"
        )?.toDomain()
    }

    override suspend fun saveLocalVersion(githubRelease: GithubRelease) {
        versionDao.insertVersion(githubRelease.toEntity())
    }*/
}