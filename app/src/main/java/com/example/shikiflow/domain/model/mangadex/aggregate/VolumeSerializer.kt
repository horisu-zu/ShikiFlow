package com.example.shikiflow.domain.model.mangadex.aggregate

import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder

object VolumesSerializer : KSerializer<Map<String, AggregateVolume>> {
    private val mapSerializer = MapSerializer(String.serializer(), AggregateVolume.serializer())
    override val descriptor = mapSerializer.descriptor

    override fun deserialize(decoder: Decoder): Map<String, AggregateVolume> {
        val element = (decoder as JsonDecoder).decodeJsonElement()
        return if (element is JsonArray) emptyMap()
        else decoder.json.decodeFromJsonElement(mapSerializer, element)
    }

    override fun serialize(encoder: Encoder, value: Map<String, AggregateVolume>) {
        mapSerializer.serialize(encoder, value)
    }
}