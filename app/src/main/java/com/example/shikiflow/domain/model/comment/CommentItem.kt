package com.example.shikiflow.domain.model.comment

import com.example.shikiflow.domain.model.common.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

import kotlin.time.Instant


@Serializable
data class CommentItem(
    val body: String,
    @SerialName("can_be_edited") val canBeEdited: Boolean,
    @SerialName("commentable_id") val commentableId: Int,
    @SerialName("commentable_type") val commentableType: String,
    @Serializable(with = InstantSerializer::class)
    @SerialName("created_at") val createdAt: Instant,
    @SerialName("html_body") val htmlBody: String,
    val id: Int,
    @SerialName("is_offtopic") val isOfftopic: Boolean,
    @SerialName("is_summary") val isSummary: Boolean,
    @Serializable(with = InstantSerializer::class)
    @SerialName("updated_at") val updatedAt: Instant,
    val user: User,
    @SerialName("user_id") val userId: Int
)