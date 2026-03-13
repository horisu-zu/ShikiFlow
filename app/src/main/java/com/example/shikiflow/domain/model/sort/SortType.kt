package com.example.shikiflow.domain.model.sort

sealed interface SortType {
    val supportsDirection: Boolean
        get() = true
}

sealed interface MediaSort : SortType {
    enum class Shikimori : MediaSort {
        RANKED_MAL,
        RANKED,
        POPULARITY,
        EPISODES,
        STATUS;

        companion object {
            fun getOngoingOrderOptions(): List<MediaSort> {
                return listOf(
                    RANKED_MAL,
                    RANKED,
                    POPULARITY
                )
            }
        }
    }

    enum class Anilist : MediaSort {
        POPULARITY,
        SCORE,
        TRENDING,
        FAVORITES,
        DATE_ADDED,
        RELEASE_DATE;

        companion object {
            fun getOngoingOrderOptions(): List<MediaSort> {
                return listOf(
                    POPULARITY,
                    SCORE,
                    TRENDING
                )
            }
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
    RELEVANCE,
    FAVORITES,
    ROLE,
    ID;

    override val supportsDirection: Boolean
        get() = this != RELEVANCE
}