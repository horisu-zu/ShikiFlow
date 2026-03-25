package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.UserStatsCategories
import com.example.shikiflow.presentation.common.TabRowItem
import com.example.shikiflow.utils.IconResource

enum class ProfileSectionType {
    USER_STATS,
    ACTIVITY,
    FAVORITES;
    /*Will add more later*/

    companion object {
        fun getTabRows(
            userStatsCategories: UserStatsCategories
        ): List<TabRowItem<ProfileSectionType>> {
            return buildList {
                if(userStatsCategories.scoreMediaTypes.isNotEmpty()) {
                    add(TabRowItem(USER_STATS, IconResource.Drawable(resId = R.drawable.ic_stats)))
                    add(TabRowItem(ACTIVITY, IconResource.Drawable(resId = R.drawable.ic_history)))
                }
                if(userStatsCategories.favoriteCategories.isNotEmpty()) {
                    add(TabRowItem(FAVORITES, IconResource.Vector(imageVector = Icons.Default.Star)))
                }
            }
        }
    }
}