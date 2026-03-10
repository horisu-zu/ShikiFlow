package com.example.shikiflow.domain.model.sort

sealed interface OrderOption

sealed interface BrowseOrder : OrderOption {
    enum class Shikimori : BrowseOrder {
        RANKED_MAL,
        RANKED,
        POPULARITY,
        EPISODES,
        STATUS;

        companion object {
            fun getOngoingOrderOptions(): List<BrowseOrder> {
                return listOf(
                    RANKED_MAL,
                    RANKED,
                    POPULARITY
                )
            }
        }
    }

    enum class Anilist : BrowseOrder {
        POPULARITY,
        SCORE,
        TRENDING,
        FAVORITES,
        DATE_ADDED,
        RELEASE_DATE;

        companion object {
            fun getOngoingOrderOptions(): List<BrowseOrder> {
                return listOf(
                    POPULARITY,
                    SCORE,
                    TRENDING
                )
            }
        }
    }
}