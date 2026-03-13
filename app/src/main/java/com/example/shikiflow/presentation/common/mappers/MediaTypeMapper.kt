package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType

object MediaTypeMapper {
    fun MediaType.displayValue(): Int {
        return when(this) {
            MediaType.ANIME -> R.string.media_type_anime
            MediaType.MANGA -> R.string.media_type_manga
        }
    }
}