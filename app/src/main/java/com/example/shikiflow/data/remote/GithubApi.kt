package com.example.shikiflow.data.remote

import com.example.shikiflow.domain.model.common.GithubRelease
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {

    @GET("/repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): GithubRelease

    @GET("/repos/{owner}/{repo}/releases/tags/v{versionTag}")
    suspend fun getReleaseByVersion(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("versionTag") versionTag: String
    ): GithubRelease
}