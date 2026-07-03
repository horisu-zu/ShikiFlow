package com.example.shikiflow.data.local.entity.thread_comment

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_short")
data class UserShortEntity(
    @PrimaryKey val id: Int,
    val nickname: String,
    val avatarUrl: String,
    val bannerUrl: String?
)
