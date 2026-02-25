package com.example.shikiflow.data.datasource.dto.mangadex.scanlation_group

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanlationGroupResponse(
    @SerialName("data") val groupDataResponse: GroupDataResponse,
    val response: String,
    val result: String
)