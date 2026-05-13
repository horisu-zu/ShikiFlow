package com.example.shikiflow.data.local.converter

import androidx.room.TypeConverter
import com.example.shikiflow.domain.model.media_details.MediaTitle
import kotlinx.serialization.json.Json

class MediaTitleConverter {
    @TypeConverter
    fun fromMediaTitle(title: MediaTitle?): String? {
        return title?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toMediaTitle(value: String?): MediaTitle? {
        return value?.let { Json.decodeFromString<MediaTitle>(it) }
    }
}