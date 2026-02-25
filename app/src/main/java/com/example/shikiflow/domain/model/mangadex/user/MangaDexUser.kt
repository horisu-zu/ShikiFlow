package com.example.shikiflow.domain.model.mangadex.user

data class MangaDexUser(
    val id: String,
    val username: String,
    val roles: List<String>
)
