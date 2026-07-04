package com.example.shikiflow.data.local.entity.thread_comment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "thread")
data class ThreadEntity(
    @PrimaryKey val id: Int,
    val title: String?,
    val body: String?,
    @ColumnInfo(defaultValue = "[]")
    val categories: List<String>,
    val viewCount: Int,
    val replyCount: Int,
    val createdAt: Instant
)
