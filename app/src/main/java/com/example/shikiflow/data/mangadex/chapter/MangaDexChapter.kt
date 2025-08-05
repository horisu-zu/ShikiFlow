package com.example.shikiflow.data.mangadex.chapter

import kotlinx.serialization.Serializable

@Serializable
data class MangaDexChapter(
    val hash: String,
    val data: List<String>,
    val dataSaver: List<String>
)
