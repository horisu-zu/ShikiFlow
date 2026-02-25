package com.example.shikiflow.domain.model.mangadex.chapter

data class MangaChapter(
    val baseUrl: String,
    val hash: String,
    val data: List<String>,
    val dataSaver: List<String>
)
