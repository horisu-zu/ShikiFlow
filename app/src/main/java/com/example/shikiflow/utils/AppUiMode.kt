package com.example.shikiflow.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import com.example.graphql.type.OrderEnum
import com.example.shikiflow.R

enum class AppUiMode {
    LIST, GRID;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: LIST
    }

    val displayValue: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }
}

enum class BrowseUiMode {
    AUTO, LIST, GRID;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: AUTO
    }

    val displayValue: String
        get() = name.lowercase().replaceFirstChar { it.uppercase() }

    val icon: IconResource
        get() = when(this) {
            AUTO -> IconResource.Drawable(resId = R.drawable.ic_stars)
            LIST -> IconResource.Vector(imageVector = Icons.AutoMirrored.Filled.List)
            GRID -> IconResource.Drawable(resId = R.drawable.ic_grid)
        }
}

enum class BrowseOngoingOrder(val orderEnum: OrderEnum) {
    RANKED(OrderEnum.ranked),
    RANKED_SHIKI(OrderEnum.ranked_shiki),
    POPULARITY(OrderEnum.popularity),
    AIRED_ON(OrderEnum.aired_on);

    companion object {
        fun fromString(value: String?) = BrowseOngoingOrder.entries.find { it.name == value } ?: RANKED
    }

    val displayValue: Int
        get() = when(this) {
            RANKED -> R.string.ongoing_browse_mode_ranked
            RANKED_SHIKI -> R.string.ongoing_browse_mode_ranked_shiki
            POPULARITY -> R.string.ongoing_browse_mode_popularity
            AIRED_ON -> R.string.ongoing_browse_mode_aired_on
        }
}