package com.example.shikiflow.data.local.converter

import androidx.room.TypeConverter
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import kotlinx.serialization.json.Json

class GenreConverter {
    @TypeConverter
    fun fromGenres(genres: List<Genre>?): String? {
        return genres?.let {
            Json.encodeToString(genres)
        }
    }

    @TypeConverter
    fun toGenres(value: String?): List<Genre>? {
        return value?.let {
            Json.decodeFromString(value)
        }
    }

    @TypeConverter
    fun fromTags(tags: List<MediaTagEnum>?): String? {
        return tags?.let {
            Json.encodeToString(tags)
        }
    }

    @TypeConverter
    fun toTags(value: String?): List<MediaTagEnum>? {
        return value?.let {
            Json.decodeFromString(value)
        }
    }
}