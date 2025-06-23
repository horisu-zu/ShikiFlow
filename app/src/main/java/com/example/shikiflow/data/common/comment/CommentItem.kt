package com.example.shikiflow.data.common.comment

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentItem(
    val body: String,
    @SerialName("can_be_edited") val canBeEdited: Boolean,
    @SerialName("commentable_id") val commentableId: Int,
    @SerialName("commentable_type") val commentableType: String,
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("html_body") val htmlBody: String,
    val id: Int,
    @SerialName("is_offtopic") val isOfftopic: Boolean,
    @SerialName("is_summary") val isSummary: Boolean,
    @SerialName("updated_at") val updatedAt: Instant,
    val user: User,
    @SerialName("user_id") val userId: Int
)