package com.example.shikiflow.data.repository

import com.example.shikiflow.data.local.dao.VersionDao
import com.example.shikiflow.data.local.entity.version.VersionDto.Companion.toDomain
import com.example.shikiflow.data.remote.GithubApi
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.domain.model.common.GithubRelease.Companion.toEntity
import com.example.shikiflow.domain.repository.GithubRepository
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val githubApi: GithubApi,
    private val versionDao: VersionDao
): GithubRepository {
    override suspend fun getLatestRelease(
        owner: String,
        repo: String
    ): GithubRelease = githubApi.getLatestRelease(owner, repo)

    override suspend fun getReleaseByVersion(
        owner: String,
        repo: String,
        versionTag: String
    ): GithubRelease? = githubApi.getReleaseByVersion(owner, repo, versionTag)

    override suspend fun getLatestLocalVersion(versionTag: String): GithubRelease? {
        return versionDao.getLatestLocalVersion(
            localVersionTag = "v$versionTag"
        )?.toDomain()
    }

    override suspend fun saveLocalVersion(githubRelease: GithubRelease) {
        versionDao.insertVersion(githubRelease.toEntity())
    }
}