package com.example.shikiflow.domain.model.mangadex.scanlation_group

import com.example.shikiflow.domain.model.mangadex.manga.AltTitle
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ScanlationGroupAttributes(
    val altNames: List<AltTitle> = emptyList(),
    val contactEmail: String?,
    @Contextual val createdAt: Instant,
    val description: String?,
    val discord: String?,
    val exLicensed: Boolean = false,
    val focusedLanguages: List<String> = emptyList(),
    val inactive: Boolean,
    val ircChannel: String?,
    val ircServer: String?,
    val locked: Boolean,
    val mangaUpdates: String?,
    val name: String,
    val official: Boolean,
    val publishDelay: String?,
    val twitter: String?,
    val updatedAt: Instant,
    val verified: Boolean,
    val version: Int,
    val website: String?
)