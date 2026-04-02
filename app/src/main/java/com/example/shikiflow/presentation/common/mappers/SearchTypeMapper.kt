package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.presentation.screen.browse.main.SearchType

object SearchTypeMapper {
    fun SearchType.displayValue(): Int {
        return when(this) {
            SearchType.MEDIA -> R.string.browse_search_label_media
            SearchType.CHARACTER -> R.string.character_title
            SearchType.STAFF -> R.string.staff_title
            SearchType.USER -> R.string.user_label
        }
    }
}