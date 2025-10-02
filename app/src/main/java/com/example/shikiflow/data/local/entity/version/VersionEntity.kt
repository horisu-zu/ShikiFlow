package com.example.shikiflow.data.local.entity.version

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity("version")
data class VersionEntity(
    @PrimaryKey val versionTag: String,
    val name: String,
    val body: String? = null,
    val publishedAt: Instant
)