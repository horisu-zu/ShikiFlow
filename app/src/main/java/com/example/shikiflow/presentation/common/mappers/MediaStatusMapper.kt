package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaStatus

object MediaStatusMapper {
    fun MediaStatus.displayValue(): Int {
        return when(this) {
            MediaStatus.ANNOUNCED -> R.string.media_status_announced
            MediaStatus.ONGOING -> R.string.media_status_ongoing
            MediaStatus.RELEASED -> R.string.media_status_released
            MediaStatus.CANCELLED -> R.string.media_status_manga_cancelled
            MediaStatus.HIATUS -> R.string.media_status_manga_hiatus
            MediaStatus.UNKNOWN -> R.string.common_unknown
        }
    }
}