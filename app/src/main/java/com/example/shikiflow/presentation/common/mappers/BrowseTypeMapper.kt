package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.BrowseType

object BrowseTypeMapper {
    fun BrowseType.displayValue(): Int {
        return when(this) {
            BrowseType.AnimeBrowseType.ONGOING -> R.string.browse_type_anime_ongoing
            BrowseType.AnimeBrowseType.ANIME_TOP -> R.string.browse_type_anime_top
            BrowseType.MangaBrowseType.MANGA_TOP -> R.string.browse_type_manga_top
            BrowseType.AnimeBrowseType.ANIME_POPULARITY -> R.string.browse_type_popular_anime
            BrowseType.MangaBrowseType.MANGA_POPULARITY -> R.string.browse_type_popular_manga
        }
    }
}