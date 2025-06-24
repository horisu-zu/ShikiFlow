package com.example.shikiflow.data.common

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExternalLink(
    @SerialName("created_at") val createdAt: Instant?,
    @SerialName("entry_id") val entryId: Int,
    @SerialName("entry_type") val entryType: String,
    val id: Int?,
    @SerialName("imported_at") val importedAt: String?,
    val kind: String,
    val source: String,
    @SerialName("updated_at") val updatedAt: Instant?,
    val url: String
)