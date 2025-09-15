package com.example.shikiflow.domain.model.common

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class GithubRelease(
    @SerialName("tag_name") val tagName: String,
    @SerialName("name") val name: String? = null,
    @SerialName("body") val body: String? = null,
    @Contextual @SerialName("published_at") val publishedAt: Instant? = null,
    val assets: List<Asset> = emptyList()
)

@Serializable
data class Asset(
    @SerialName("browser_download_url") val downloadUrl: String,
    @SerialName("size") val size: Long
)
