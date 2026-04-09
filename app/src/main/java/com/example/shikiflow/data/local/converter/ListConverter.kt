package com.example.shikiflow.data.local.converter

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class ListConverter {
    @TypeConverter
    fun fromList(value: List<String>?): String? {
        return value?.let { Json.encodeToString(it) }
    }

    @TypeConverter
    fun toList(value: String?): List<String>? {
        return value?.let { Json.decodeFromString(it) }
    }
}