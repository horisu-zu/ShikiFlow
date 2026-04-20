package com.example.shikiflow.domain.model.sort

sealed interface SortType {
    val supportsDirection: Boolean
        get() = true
}

sealed interface MediaSort : SortType {
    val name: String

    enum class Common : MediaSort {
        SCORE,
        POPULARITY
    }

    enum class Shikimori : MediaSort {
        RANKED,
        EPISODES,
        STATUS;

        companion object {
            val ongoingOptions = listOf<MediaSort>(
                Common.SCORE,
                RANKED,
                Common.POPULARITY
            )

            fun from(name: String?): MediaSort =
                entries.find { it.name == name }
                    ?: Common.entries.find { it.name == name }
                    ?: Common.SCORE
        }
    }

    enum class Anilist : MediaSort {
        TRENDING,
        FAVORITES,
        DATE_ADDED,
        RELEASE_DATE;

        companion object {
            val ongoingOptions = listOf<MediaSort>(
                Common.POPULARITY,
                Common.SCORE,
                TRENDING
            )

            fun from(name: String?): MediaSort =
                entries.find { it.name == name }
                    ?: Common.entries.find { it.name == name }
                    ?: Common.POPULARITY
        }
    }
}

enum class ThreadType : SortType {
    CREATED_AT,
    REPLIED_AT,
    TITLE,
    REPLY_COUNT,
    VIEW_COUNT
}

enum class UserRateType : SortType {
    ID,
    ADDED_AT,
    UPDATED_AT,
    SCORE,
    PROGRESS
}

enum class StaffType : SortType {
    RELEVANCE,
    ID,
    ROLE,
    FAVORITES;

    override val supportsDirection: Boolean
        get() = this != RELEVANCE
}

enum class CharacterType : SortType {
    FAVORITES,
    RELEVANCE,
    ROLE,
    ID;

    override val supportsDirection: Boolean
        get() = this != RELEVANCE
}

enum class ReviewType : SortType {
    ID,
    SCORE,
    RATING,
    CREATED_AT,
    UPDATED_AT
}