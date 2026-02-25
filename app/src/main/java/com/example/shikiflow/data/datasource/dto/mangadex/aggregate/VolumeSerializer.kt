package com.example.shikiflow.data.datasource.dto.mangadex.aggregate

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject

object VolumesSerializer : KSerializer<Map<String, AggregateVolumeResponse>> {
    private val mapSerializer = MapSerializer(String.serializer(), AggregateVolumeResponse.serializer())
    private val listSerializer = ListSerializer(AggregateVolumeResponse.serializer())
    override val descriptor = mapSerializer.descriptor

    override fun deserialize(decoder: Decoder): Map<String, AggregateVolumeResponse> {
        val element = (decoder as JsonDecoder).decodeJsonElement()

        return when(element) {
            is JsonArray -> {
                val volumes = decoder.json.decodeFromJsonElement(listSerializer, element)
                volumes.associateBy { it.volume }
            }
            is JsonObject -> {
                decoder.json.decodeFromJsonElement(mapSerializer, element)
            }
            else -> emptyMap()
        }
    }

    override fun serialize(encoder: Encoder, value: Map<String, AggregateVolumeResponse>) {
        mapSerializer.serialize(encoder, value)
    }
}