package com.example.shikiflow.presentation.screen.main

import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.TabRowItem
import com.example.shikiflow.utils.IconResource

enum class TracksFilterType {
    SORT,
    GENRES;
    //maybe I'll add tags later too

    companion object {
        fun TracksFilterType.displayValue(): Int {
            return when(this) {
                SORT -> R.string.browse_search_label_sort_by
                GENRES -> R.string.browse_search_label_genre
            }
        }

        fun TracksFilterType.iconResource(): IconResource {
            return when(this) {
                SORT -> IconResource.Drawable(resId = R.drawable.ic_sort_vertical)
                GENRES -> IconResource.Drawable(resId = R.drawable.ic_masks)
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