package com.example.shikiflow.data.datasource.dto.comment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiCreateComment(
    val body: String,
    @SerialName("commentable_id") val commentableId: Int,
    @SerialName("commentable_type") val commentableType: String = "Topic",
    @SerialName("is_offtopic") val isOfftopic: Boolean = false
)

@Serializable
data class ShikiUpdateComment(
    val body: String
)
