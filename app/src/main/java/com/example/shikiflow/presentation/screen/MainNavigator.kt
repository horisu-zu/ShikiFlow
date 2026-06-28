package com.example.shikiflow.presentation.screen

import android.content.res.Configuration
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
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.material3.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import com.example.shikiflow.presentation.navigation.BottomNavItem
import com.example.shikiflow.presentation.screen.browse.BrowseNavRoute
import com.example.shikiflow.presentation.screen.browse.BrowseScreenNavigator
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.screen.main.MainScreenNavigator
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavRoute
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavigator
import com.example.shikiflow.presentation.viewmodel.MainNavViewModel
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainNavigator(
    onMoveToBack: () -> Unit,
    mainNavViewModel: MainNavViewModel = hiltViewModel()
) {
    val configuration = LocalConfiguration.current
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val navigationSuiteState = rememberNavigationSuiteScaffoldState()

    val preferredTitleType by mainNavViewModel.preferredTitleType.collectAsStateWithLifecycle()

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
        isExpanded -> NavigationSuiteType.NavigationRail
        else -> NavigationSuiteScaffoldDefaults.navigationSuiteType(adaptiveInfo)
    }

    val showNavigation by remember(mainNavBackStack, isKeyboardVisible, isBottomBarVisible) {
        derivedStateOf {
            !isKeyboardVisible && isBottomBarVisible
        }
    }

    LaunchedEffect(showNavigation) {
        when(showNavigation) {
            true -> navigationSuiteState.show()
            false -> navigationSuiteState.hide()
        }
    }

    LaunchedEffect(Unit) {
        mainNavViewModel.authEvent.collect {
            listOf(mainScreenBackStack, browseBackStack).forEach { backStack ->
                backStack.subList(1, backStack.size).clear()
            }
        }
    }

    NavigationSuiteScaffold(
        state = navigationSuiteState,
        navigationSuiteType = customNavType,
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            navigationBarContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            navigationRailContainerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        navigationItems = {
            BottomNavItem.items.forEach { navItem ->
                val isSelected = mainNavBackStack.last() == navItem.route

                NavigationSuiteItem(
                    icon = {
                        when (isSelected) {
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
            NavBarState { show -> isBottomBarVisible = show }
        }

        CompositionLocalProvider(
            LocalNavBarController provides controller,
            LocalTitleTypeController provides preferredTitleType
        ) {
            NavDisplay(
                backStack = mainNavBackStack,
                onBack = { onMoveToBack() },
                entryProvider = entryProvider {
                    entry<MainNavRoute.Main> {
                        MainScreenNavigator(
                            mainScreenBackStack = mainScreenBackStack
                        )
                    }
                    entry<MainNavRoute.Browse> {
                        BrowseScreenNavigator(
                            browseBackStack = browseBackStack
                        )
                    }
                    entry<MainNavRoute.Profile> {
                        ProfileNavigator(
                            profileBackstack = profileBackstack
                        )
                    }
                },
                transitionSpec = {
                    slideInVertically(
                        initialOffsetY = { it / 2 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) + fadeIn() togetherWith  ExitTransition.None
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