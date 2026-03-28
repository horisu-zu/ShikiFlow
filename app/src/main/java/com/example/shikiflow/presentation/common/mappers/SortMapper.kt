package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.sort.StaffType
import com.example.shikiflow.domain.model.sort.ThreadType
import com.example.shikiflow.domain.model.sort.UserRateType

object SortMapper {
    fun SortType.displayValue(): Int {
        return when(this) {
            MediaSort.Common.SCORE, UserRateType.SCORE -> R.string.ongoing_browse_mode_score
            MediaSort.Common.POPULARITY -> R.string.ongoing_browse_mode_popularity
            MediaSort.Shikimori.RANKED -> R.string.ongoing_browse_mode_score_shiki
            MediaSort.Shikimori.EPISODES -> R.string.browse_order_episodes_count
            MediaSort.Shikimori.STATUS -> R.string.browse_order_status
            MediaSort.Anilist.TRENDING -> R.string.ongoing_browse_mode_trending
            MediaSort.Anilist.FAVORITES, StaffType.FAVORITES, CharacterType.FAVORITES -> R.string.browse_order_favorites
            MediaSort.Anilist.DATE_ADDED, UserRateType.ADDED_AT -> R.string.browse_order_date_added
            MediaSort.Anilist.RELEASE_DATE -> R.string.browse_order_release_date
            StaffType.ID, UserRateType.ID, CharacterType.ID -> R.string.sort_type_id
            StaffType.ROLE, CharacterType.ROLE -> R.string.sort_type_role
            StaffType.RELEVANCE, CharacterType.RELEVANCE -> R.string.sort_type_relevance
            ThreadType.TITLE -> R.string.sort_type_title
            ThreadType.CREATED_AT -> R.string.sort_type_created_at
            ThreadType.REPLIED_AT -> R.string.sort_type_replied_at
            ThreadType.REPLY_COUNT -> R.string.sort_type_reply_count
            ThreadType.VIEW_COUNT -> R.string.sort_type_view_count
            UserRateType.UPDATED_AT -> R.string.sort_type_updated_at
            UserRateType.PROGRESS -> R.string.sort_type_progress
        }
    }

    fun SortDirection.displayValue(): Int {
        return when(this) {
            SortDirection.ASCENDING -> R.string.sort_direction_ascending
            SortDirection.DESCENDING ->  R.string.sort_direction_descending
        }
    }
}