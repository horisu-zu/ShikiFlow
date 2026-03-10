package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType

object MediaTypeMapper {
    fun MediaType.displayValue(): Int {
        return when(this) {
            MediaType.ANIME -> R.string.browse_search_media_anime
            MediaType.MANGA -> R.string.browse_search_media_manga
        }
    }
}