package com.example.shikiflow.domain.model.comment

data class EntityData(
    val id: String,
    val type: EntityType
)

enum class EntityType {
    CHARACTER,
    PERSON,
    ANIME,
    MANGA,
    RANOBE,
    COMMENT
}