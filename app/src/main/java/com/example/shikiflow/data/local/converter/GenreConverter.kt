package com.example.shikiflow.data.local.converter

import androidx.room.TypeConverter
import com.example.shikiflow.domain.model.media_details.Genre
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
}