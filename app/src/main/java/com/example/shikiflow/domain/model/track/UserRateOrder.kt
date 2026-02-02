package com.example.shikiflow.domain.model.track

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.SortDirection

data class UserRateOrder(
    val type: UserRateOrderType,
    val sort: SortDirection
)

enum class UserRateOrderType {
    ID,
    ADDED_AT,
    UPDATED_AT,
    SCORE,
    PROGRESS
}

sealed interface OrderOption {
    val displayValue: Int
}

sealed interface BrowseOrder : OrderOption {
    enum class Shikimori(override val displayValue: Int) : BrowseOrder {
        RANKED_MAL(R.string.ongoing_browse_mode_ranked),
        RANKED(R.string.ongoing_browse_mode_ranked_shiki),
        POPULARITY(R.string.ongoing_browse_mode_popularity),
        EPISODES(R.string.browse_order_episodes_count),
        STATUS(R.string.browse_order_status);

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

    enum class Anilist(override val displayValue: Int) : BrowseOrder {
        POPULARITY(R.string.ongoing_browse_mode_popularity),
        SCORE(R.string.ongoing_browse_mode_score),
        TRENDING(R.string.ongoing_browse_mode_trending),
        FAVORITES(R.string.browse_order_favorites),
        DATE_ADDED(R.string.browse_order_date_added),
        RELEASE_DATE(R.string.browse_order_release_date);

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