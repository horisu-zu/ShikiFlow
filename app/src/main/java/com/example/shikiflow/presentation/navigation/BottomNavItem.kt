package com.example.shikiflow.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.R
import com.example.shikiflow.presentation.screen.MainNavRoute
import com.example.shikiflow.utils.IconResource

sealed class BottomNavItem(
    val title: Int,
    val selectedIconRes: IconResource,
    val unselectedIconRes: IconResource,
    val route: MainNavRoute
): NavKey {
    object Main : BottomNavItem(
        title = R.string.bottom_nav_item_main,
        selectedIconRes = IconResource.Drawable(R.drawable.ic_selected_book),
        unselectedIconRes = IconResource.Drawable(R.drawable.ic_unselected_book),
        route = MainNavRoute.Main
    )

    object Browse : BottomNavItem(
        title = R.string.bottom_nav_item_browse,
        selectedIconRes = IconResource.Drawable(R.drawable.ic_selected_browse),
        unselectedIconRes = IconResource.Drawable(R.drawable.ic_unselected_browse),
        route = MainNavRoute.Browse
    )

    object Profile : BottomNavItem(
        title = R.string.bottom_nav_item_profile,
        selectedIconRes = IconResource.Vector(Icons.Default.Person),
        unselectedIconRes = IconResource.Vector(Icons.Outlined.Person),
        route = MainNavRoute.Profile
    )

    companion object {
        val items = listOf(Main, Browse, Profile)

        fun NavKey.isBottomNavItem() = items.any { it.route == this }
    }
}