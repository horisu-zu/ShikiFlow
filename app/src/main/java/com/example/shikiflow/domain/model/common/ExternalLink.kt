package com.example.shikiflow.domain.model.common

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ExternalLink(
    @Contextual @SerialName("created_at") val createdAt: Instant?,
    @SerialName("entry_id") val entryId: Int,
    @SerialName("entry_type") val entryType: String,
    val id: Int?,
    @SerialName("imported_at") val importedAt: String?,
    val kind: String,
    val source: String,
    @SerialName("updated_at") val updatedAt: Instant?,
    val url: String
)