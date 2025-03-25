package com.example.shikiflow.data.tracks

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Refresh
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.R
import com.example.shikiflow.utils.IconResource

enum class AnimeStatus(val icon: IconResource) {
    PLANNED(IconResource.Vector(Icons.Outlined.DateRange)),
    WATCHING(IconResource.Vector(Icons.Outlined.PlayArrow)),
    COMPLETED(IconResource.Drawable(R.drawable.ic_completed)),
    REWATCHING(IconResource.Vector(Icons.Outlined.Refresh)),
    ON_HOLD(IconResource.Drawable(R.drawable.ic_pause)),
    DROPPED(IconResource.Vector(Icons.Outlined.Clear)),
    UNKNOWN__(IconResource.Drawable(R.drawable.ic_bookmark));

    companion object {
        fun fromStatus(status: UserRateStatusEnum): AnimeStatus? {
            return entries.find { it.name.equals(status.name, ignoreCase = true) }
        }
    }
}