package com.example.shikiflow.utils

import com.apollographql.apollo.api.Adapter
import com.apollographql.apollo.api.CustomScalarAdapters
import com.apollographql.apollo.api.json.JsonReader
import com.apollographql.apollo.api.json.JsonWriter

object AdapterFactory {
    inline fun <reified T : Enum<T>> createAdapter(): Adapter<T> {
        return object : AutoGraphQLAdapter<T>(T::class.java) {}
    }

    abstract class AutoGraphQLAdapter<T : Enum<T>>(private val enumClass: Class<T>) : Adapter<T> {
        override fun fromJson(reader: JsonReader, customScalarAdapters: CustomScalarAdapters): T {
            val value = reader.nextString()
            return enumClass.enumConstants?.find {
                it.name.lowercase().replace("_", "") == value?.lowercase()?.replace("/", "")?.replace("_", "")
            } ?: throw IllegalArgumentException("Unknown value: $value for ${enumClass.simpleName}")
        }

        override fun toJson(writer: JsonWriter, customScalarAdapters: CustomScalarAdapters, value: T) {
            writer.value(value.name.lowercase().replace("_", ""))
        }
    }
}