package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.TabRowItem
import com.example.shikiflow.utils.IconResource

enum class ProfileSectionType {
    OVERVIEW,
    FAVORITES;
    /*Will add more later*/

    companion object {
        fun getTabRows(
            hasEntries: Boolean,
            hasFavorites: Boolean
        ): List<TabRowItem<ProfileSectionType>> {
            return buildList {
                if(hasEntries) {
                    add(TabRowItem(OVERVIEW, IconResource.Drawable(R.drawable.ic_stats)))
                }
                if(hasFavorites) {
                    add(TabRowItem(FAVORITES, IconResource.Vector(Icons.Default.Star)))
                }
            }
        }
    }
}