package com.example.shikiflow.domain.model.mangadex.scanlation_group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanlationGroupResponse(
    @SerialName("data") val groupData: GroupData,
    val response: String,
    val result: String
)