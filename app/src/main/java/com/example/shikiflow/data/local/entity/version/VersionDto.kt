package com.example.shikiflow.data.local.entity.version

import androidx.room.Embedded
import androidx.room.Relation
import com.example.shikiflow.data.local.entity.version.AssetsEntity.Companion.toDomain
import com.example.shikiflow.domain.model.common.GithubRelease

data class VersionDto(
    @Embedded val version: VersionEntity,
    @Relation(
        parentColumn = "versionTag",
        entityColumn = "versionTagRef"
    )
    val assets: List<AssetsEntity>
) {
    companion object {
        fun VersionDto.toDomain(): GithubRelease {
            return GithubRelease(
                tagName = this.version.versionTag,
                name = this.version.name,
                body = this.version.body,
                publishedAt = this.version.publishedAt,
                assets = this.assets.map { assetEntity -> assetEntity.toDomain() }
            )
        }
    }
}
