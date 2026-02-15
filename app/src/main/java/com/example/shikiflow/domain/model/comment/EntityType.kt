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
    COMMENT;

    companion object {
        fun String.getAnilistEntityType(): EntityType? {
            return when(this) {
                "anime" -> ANIME
                "manga" -> MANGA
                "character" -> CHARACTER
                "staff" -> PERSON
                else -> null
            }
        }
    }
}