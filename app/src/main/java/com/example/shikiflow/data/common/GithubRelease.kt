package com.example.shikiflow.data.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubRelease(
    @SerialName("tag_name") val tagName: String,
    @SerialName("name") val name: String? = null,
    @SerialName("body") val body: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    val assets: List<Asset> = emptyList()
)

@Serializable
data class Asset(
    @SerialName("browser_download_url") val downloadUrl: String
)
