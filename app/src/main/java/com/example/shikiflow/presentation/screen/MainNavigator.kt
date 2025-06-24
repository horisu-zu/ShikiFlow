package com.example.shikiflow.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.R
import com.example.shikiflow.presentation.screen.browse.BrowseScreenNavigator
import com.example.shikiflow.presentation.screen.main.MainScreenNavigator
import com.example.shikiflow.presentation.screen.more.MoreScreenNavigator
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel
import com.example.shikiflow.utils.AppSettingsManager

@Composable
fun MainNavigator(
    appSettingsManager: AppSettingsManager,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Browse,
        BottomNavItem.More
    )
    val mainNavBackStack = rememberNavBackStack(MainNavRoute.Home)
    val currentUser by userViewModel.currentUserData.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                items.forEach { navItem ->
                    val isSelected = mainNavBackStack.last() == navItem.route

                    NavigationBarItem(
                        icon = {
                            Icon(
                                painterResource(
                                    id = if (isSelected) navItem.selectedIconRes
                                        else navItem.unselectedIconRes
                                ),
                                contentDescription = navItem.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(navItem.title) },
                        selected = isSelected,
                        onClick = {
                            if (!isSelected) {
                                mainNavBackStack.add(navItem.route)
                                //topLevelBackStack.switchTopLevel(topLevelRoute.route)
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = mainNavBackStack,
            onBack = { mainNavBackStack.removeLastOrNull() },
            modifier = Modifier.padding(
                bottom = innerPadding.calculateBottomPadding()
            ),
            entryProvider = entryProvider {
                entry<MainNavRoute.Home> {
                    MainScreenNavigator(
                        appSettingsManager = appSettingsManager,
                        currentUserData = currentUser.data
                    )
                }
                entry<MainNavRoute.Browse> {
                    BrowseScreenNavigator(
                        currentUserData = currentUser.data
                    )
                }
                entry<MainNavRoute.More> {
                    MoreScreenNavigator(currentUser = currentUser.data)
                }
            },
            transitionSpec = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(
                    initialAlpha = 0.1f,
                    animationSpec = tween(500)
                ) togetherWith ExitTransition.KeepUntilTransitionsFinished
            },
            popTransitionSpec = {
                EnterTransition.None togetherWith slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(targetAlpha = 0.1f, animationSpec = tween(500))
            },
            predictivePopTransitionSpec = {
                EnterTransition.None togetherWith slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(targetAlpha = 0.1f, animationSpec = tween(500))
            }
        )
    }
}

sealed class BottomNavItem(
    var title: String,
    @DrawableRes var selectedIconRes: Int,
    @DrawableRes var unselectedIconRes: Int,
    var route: MainNavRoute
): NavKey {
    object Home : BottomNavItem(
        "Main",
        R.drawable.ic_selected_book,
        R.drawable.ic_unselected_book,
        MainNavRoute.Home
    )

    object Browse : BottomNavItem(
        "Browse",
        R.drawable.ic_selected_browse,
        R.drawable.ic_unselected_browse,
        MainNavRoute.Browse
    )

    object More : BottomNavItem(
        "More",
        R.drawable.ic_selected_dots,
        R.drawable.ic_unselected_dots,
        MainNavRoute.More
    )
}