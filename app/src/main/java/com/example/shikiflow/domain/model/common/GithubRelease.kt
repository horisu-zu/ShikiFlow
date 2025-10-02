package com.example.shikiflow.domain.model.common

import com.example.shikiflow.data.local.entity.version.AssetsEntity
import com.example.shikiflow.data.local.entity.version.VersionDto
import com.example.shikiflow.data.local.entity.version.VersionEntity
import com.example.shikiflow.domain.model.common.Asset.Companion.toEntity
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class GithubRelease(
    @SerialName("tag_name") val tagName: String,
    @SerialName("name") val name: String,
    @SerialName("body") val body: String? = null,
    @Contextual @SerialName("published_at") val publishedAt: Instant,
    val assets: List<Asset> = emptyList()
) {
    companion object {
        fun GithubRelease.toEntity(): VersionDto {
            return VersionDto(
                version = this.toVersionEntity(),
                assets = this.assets.map { it.toEntity(this.tagName) }
            )
        }
        fun GithubRelease.toVersionEntity(): VersionEntity {
            return VersionEntity(
                versionTag = this.tagName,
                name = this.name,
                body = this.body,
                publishedAt = this.publishedAt,
            )
        }
    }
}

@Serializable
data class Asset(
    @SerialName("browser_download_url") val downloadUrl: String,
    @SerialName("size") val size: Long
) {
    companion object {
        fun Asset.toEntity(versionTag: String): AssetsEntity {
            return AssetsEntity(
                downloadUrl = this.downloadUrl,
                versionTagRef = versionTag,
                size = this.size
            )
        }
    }
}
