package com.example.shikiflow.domain.model.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

object ShikiImageSerializer : KSerializer<ShikiImage> {
    override val descriptor = PrimitiveSerialDescriptor("ShikiImage", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ShikiImage) {
        encoder.encodeString(value.original ?: "")
    }

    override fun deserialize(decoder: Decoder): ShikiImage {
        return when (val element = decoder.decodeSerializableValue(JsonElement.serializer())) {
            is JsonPrimitive -> {
                ShikiImage(original = element.content)
            }
            is JsonObject -> {
                Json.decodeFromJsonElement(ShikiImage.serializer(), element)
            }
            else -> ShikiImage()
        }
    }
}