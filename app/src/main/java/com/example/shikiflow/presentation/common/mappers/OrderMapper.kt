package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.sort.BrowseOrder

object OrderMapper {
    fun BrowseOrder.displayValue(): Int {
        return when(this) {
            BrowseOrder.Shikimori.RANKED_MAL -> R.string.ongoing_browse_mode_ranked
            BrowseOrder.Shikimori.RANKED -> R.string.ongoing_browse_mode_ranked_shiki
            BrowseOrder.Shikimori.POPULARITY -> R.string.ongoing_browse_mode_popularity
            BrowseOrder.Shikimori.EPISODES -> R.string.browse_order_episodes_count
            BrowseOrder.Shikimori.STATUS -> R.string.browse_order_status
            BrowseOrder.Anilist.POPULARITY -> R.string.ongoing_browse_mode_popularity
            BrowseOrder.Anilist.SCORE -> R.string.ongoing_browse_mode_score
            BrowseOrder.Anilist.TRENDING -> R.string.ongoing_browse_mode_trending
            BrowseOrder.Anilist.FAVORITES -> R.string.browse_order_favorites
            BrowseOrder.Anilist.DATE_ADDED -> R.string.browse_order_date_added
            BrowseOrder.Anilist.RELEASE_DATE -> R.string.browse_order_release_date
        }
    }
}