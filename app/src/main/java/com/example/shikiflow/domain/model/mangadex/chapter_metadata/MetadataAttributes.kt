package com.example.shikiflow.domain.model.mangadex.chapter_metadata

import kotlin.time.Instant

data class MetadataAttributes(
    val title: String? = null,
    val volume: String? = null,
    val chapter: String? = null,
    val pages: Int,
    val translatedLanguage: String,
    val uploader: String? = null,
    val externalUrl: String? = null,
    val version: Int,
    val createdAt: String,
    val updatedAt: String,
    val publishAt: Instant,
    val readableAt: String,
    val isUnavailable: Boolean
)
