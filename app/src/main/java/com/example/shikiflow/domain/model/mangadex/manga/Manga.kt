package com.example.shikiflow.domain.model.mangadex.manga

data class Manga(
    val id: String,
    val attributes: MangaAttributes,
    val relationships: List<MangaRelationship>,
    val type: String
)
