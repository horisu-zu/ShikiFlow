package com.example.shikiflow.presentation.screen

import android.content.res.Configuration
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import com.example.shikiflow.R
import com.example.shikiflow.presentation.screen.BottomNavItem.Companion.isBottomNavItem
import com.example.shikiflow.presentation.screen.browse.BrowseNavRoute
import com.example.shikiflow.presentation.screen.browse.BrowseScreenNavigator
import com.example.shikiflow.presentation.screen.main.MainScreenNavigator
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavRoute
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavigator
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MainNavigator(
    onMoveToBack: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val adaptiveInfo = currentWindowAdaptiveInfo()

    val items = BottomNavItem.items
    val mainNavBackStack = rememberNavBackStack(MainNavRoute.Main)
    val mainScreenBackStack = rememberNavBackStack(MainScreenNavRoute.MainTracks)
    val browseBackStack = rememberNavBackStack(BrowseNavRoute.BrowseScreen)
    val profileBackstack = rememberNavBackStack(ProfileNavRoute.Profile(null))

    val isKeyboardVisible = WindowInsets.isImeVisible &&
        configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    var isBottomBarVisible by remember { mutableStateOf(true) }

    val isExpanded = adaptiveInfo.windowSizeClass.isWidthAtLeastBreakpoint(
        widthDpBreakpoint = WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND
    )

    val customNavType = when {
        isKeyboardVisible || !isBottomBarVisible -> NavigationSuiteType.None
        isExpanded -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteScaffoldDefaults.navigationSuiteType(adaptiveInfo)
    }

    NavigationSuiteScaffold(
        navigationSuiteType = customNavType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            navigationRailContainerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        navigationItems = {
            items.forEach { navItem ->
                val isSelected = mainNavBackStack.last() == navItem.route

                NavigationSuiteItem(
                    icon = {
                        when(isSelected) {
                            true -> navItem.selectedIconRes
                            false -> navItem.unselectedIconRes
                        }.toIcon(
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = stringResource(id = navItem.title),
                            style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if(!isSelected) {
                            if(mainNavBackStack.contains(navItem.route)) {
                                mainNavBackStack.remove(navItem.route)
                            }
                            mainNavBackStack.add(navItem.route)
                        } else {
                            when(navItem) {
                                BottomNavItem.Main -> mainScreenBackStack
                                BottomNavItem.Browse -> browseBackStack
                                BottomNavItem.Profile -> profileBackstack
                            }.let { backstack ->
                                backstack.subList(1, backstack.size).clear()
                            }
                        }
                    }
                )
            }
        }
    ) {
        val controller = remember {
            BottomBarState { show -> isBottomBarVisible = show }
        }

        CompositionLocalProvider(LocalBottomBarController provides controller) {
            NavDisplay(
                backStack = mainNavBackStack,
                onBack = {
                    if(mainNavBackStack.last().isBottomNavItem()) {
                        onMoveToBack()
                    } else {
                        mainNavBackStack.removeLastOrNull()
                    }
                },
                entryProvider = entryProvider {
                    entry<MainNavRoute.Main>(
                        metadata = BottomNavItem.topNavigationTransitionSpec
                    ) {
                        MainScreenNavigator(
                            mainScreenBackStack = mainScreenBackStack
                        )
                    }
                    entry<MainNavRoute.Browse>(
                        metadata = BottomNavItem.topNavigationTransitionSpec
                    ) {
                        BrowseScreenNavigator(
                            browseBackStack = browseBackStack
                        )
                    }
                    entry<MainNavRoute.Profile>(
                        metadata = BottomNavItem.topNavigationTransitionSpec
                    ) {
                        ProfileNavigator(
                            profileBackstack = profileBackstack
                        )
                    }
                },
                transitionSpec = {
                    fadeIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) togetherWith fadeOut(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                },
                popTransitionSpec = {
                    fadeIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) togetherWith fadeOut(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                },
                predictivePopTransitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                },
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                )
            )
        }
    }
}

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

        val topNavigationTransitionSpec = NavDisplay.transitionSpec {
            slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) + fadeIn() togetherWith  ExitTransition.None
        } + NavDisplay.popTransitionSpec {
            EnterTransition.None togetherWith fadeOut()
        } + NavDisplay.predictivePopTransitionSpec {
            EnterTransition.None togetherWith fadeOut()
        }
    }
}