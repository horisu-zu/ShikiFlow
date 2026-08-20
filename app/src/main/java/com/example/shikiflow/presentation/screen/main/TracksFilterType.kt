package com.example.shikiflow.presentation.screen.main

import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.TabRowItem
import com.example.shikiflow.utils.IconResource

enum class TracksFilterType {
    SORT,
    GENRES,
    TAGS,
    CUSTOM_LISTS;

    companion object {
        fun TracksFilterType.displayValue(): Int {
            return when(this) {
                SORT -> R.string.browse_search_label_sort_by
                GENRES -> R.string.browse_search_label_genre
                TAGS -> R.string.browse_search_label_tag
                CUSTOM_LISTS -> R.string.settings_custom_lists_label
            }
        }

        fun TracksFilterType.iconResource(): IconResource {
            return when(this) {
                SORT -> IconResource.Drawable(resId = R.drawable.ic_sort_vertical)
                GENRES -> IconResource.Drawable(resId = R.drawable.ic_masks)
                TAGS -> IconResource.Drawable(resId = R.drawable.ic_hashtag)
                CUSTOM_LISTS -> IconResource.Drawable(resId = R.drawable.ic_format_list)
            }
        }

        fun TracksFilterType.tabRowItem(): TabRowItem<TracksFilterType> {
            return TabRowItem(
                value = this,
                iconResource = iconResource(),
                titleRes = displayValue()
            )
        }
    }
}