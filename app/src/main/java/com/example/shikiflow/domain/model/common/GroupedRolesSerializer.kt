package com.example.shikiflow.domain.model.common

import com.example.shikiflow.domain.model.person.GroupedRole
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive

object GroupedRolesSerializer : KSerializer<List<GroupedRole>> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("GroupedRoleList") {
        element<JsonArray>("roles")
    }

    override fun deserialize(decoder: Decoder): List<GroupedRole> {
        require(decoder is JsonDecoder) { "This serializer can be used only with Json format" }

        val jsonArray = decoder.decodeJsonElement().jsonArray

        return jsonArray.map { element ->
            val roleArray = element.jsonArray
            GroupedRole(
                role = roleArray[0].jsonPrimitive.content,
                count = roleArray[1].jsonPrimitive.int
            )
        }
    }

    override fun serialize(encoder: Encoder, value: List<GroupedRole>) {
        require(encoder is JsonEncoder) { "This serializer can be used only with Json format" }

        val jsonArray = JsonArray(
            value.map { role ->
                JsonArray(listOf(
                    JsonPrimitive(role.role),
                    JsonPrimitive(role.count)
                ))
            }
        )

        encoder.encodeJsonElement(jsonArray)
    }
}