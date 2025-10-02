package com.example.shikiflow.data.local.entity.version

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.shikiflow.domain.model.common.Asset

@Entity(
    tableName = "assets",
    foreignKeys = [
        ForeignKey(
            entity = VersionEntity::class,
            parentColumns = ["versionTag"],
            childColumns = ["versionTagRef"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AssetsEntity(
    @PrimaryKey val versionTagRef: String,
    val downloadUrl: String,
    val size: Long
) {
    companion object {
        fun AssetsEntity.toDomain(): Asset {
            return Asset(
                downloadUrl = this.downloadUrl,
                size = this.size
            )
        }
    }
}