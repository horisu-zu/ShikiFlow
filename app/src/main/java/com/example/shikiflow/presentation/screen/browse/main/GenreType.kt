package com.example.shikiflow.presentation.screen.browse.main

import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.TabRowItem
import com.example.shikiflow.utils.IconResource

enum class GenreType {
    GENRE,
    TAG;

    companion object {
        fun GenreType.displayValue(): Int {
            return when(this) {
                GENRE -> R.string.browse_search_label_genre
                TAG -> R.string.browse_search_label_tag
            }
        }

        fun GenreType.iconResource(): IconResource {
            return when(this) {
                GENRE -> IconResource.Drawable(R.drawable.ic_masks)
                TAG -> IconResource.Drawable(R.drawable.ic_hashtag)
            }
        }

        fun GenreType.tabRowItem(): TabRowItem<GenreType> {
            return TabRowItem(
                value = this,
                iconResource = iconResource(),
                titleRes = displayValue()
            )
        }
    }
}