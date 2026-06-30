package com.example.shikiflow.data.local.entity.keys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey
    val key: String,
    val prevKey: Int?,
    val nextKey: Int?
)
