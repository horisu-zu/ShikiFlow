package com.example.shikiflow.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.shikiflow.R
import com.example.shikiflow.presentation.navigation.AppNavOptions
import com.example.shikiflow.presentation.screen.browse.BrowseNavRoute
import com.example.shikiflow.presentation.screen.browse.BrowseScreenNavigator
import com.example.shikiflow.presentation.screen.main.MainScreenNavigator
import com.example.shikiflow.presentation.screen.more.MoreNavRoute
import com.example.shikiflow.presentation.screen.more.MoreScreenNavigator
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainNavigator(
    appNavOptions: AppNavOptions,
    onFinishActivity: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Browse,
        BottomNavItem.More
    )
    val mainNavBackStack = rememberNavBackStack(MainNavRoute.Home)
    val mainScreenBackStack = rememberNavBackStack(MainScreenNavRoute.MainTracks)
    val browseScreenBackStack = rememberNavBackStack(BrowseNavRoute.BrowseScreen)
    val moreScreenBackStack = rememberNavBackStack(MoreNavRoute.MoreScreen)

    val currentUser by userViewModel.userFlow.collectAsState()
    val isKeyboardVisible = WindowInsets.isImeVisible

    LaunchedEffect(Unit) {
        userViewModel.fetchCurrentUser()
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = !isKeyboardVisible,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(200)
                ) + fadeIn(),
                exit = fadeOut(animationSpec = tween(50))
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    items.forEach { navItem ->
                        val isSelected = mainNavBackStack.last() == navItem.route

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painter = painterResource(
                                        id = if (isSelected) navItem.selectedIconRes
                                            else navItem.unselectedIconRes
                                    ),
                                    contentDescription = stringResource(id = navItem.title),
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = {
                                Text(
                                    text = stringResource(id = navItem.title),
                                    style = LocalTextStyle.current.copy(
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            },
                            selected = isSelected,
                            onClick = {
                                if (!isSelected) {
                                    mainNavBackStack.add(navItem.route)
                                } else {
                                    when(navItem) {
                                        BottomNavItem.Home -> {
                                            mainScreenBackStack.subList(1, mainScreenBackStack.size).clear()
                                        }
                                        BottomNavItem.Browse -> {
                                            browseScreenBackStack.subList(1, browseScreenBackStack.size).clear()
                                        }
                                        BottomNavItem.More -> {
                                            moreScreenBackStack.subList(1, moreScreenBackStack.size).clear()
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavDisplay(
            backStack = mainNavBackStack,
            onBack = { onFinishActivity() },
            modifier = Modifier.padding(
                bottom = if (isKeyboardVisible) 0.dp else paddingValues.calculateBottomPadding()
            ),
            entryProvider = entryProvider {
                entry<MainNavRoute.Home> {
                    MainScreenNavigator(
                        mainScreenBackStack = mainScreenBackStack,
                        currentUserData = currentUser,
                        onEpisodeNavigate = { title, link, translationGroup, serialNum, episodesCount ->
                            appNavOptions.navigateToPlayer(title, link, translationGroup, serialNum, episodesCount = episodesCount)
                        }
                    )
                }
                entry<MainNavRoute.Browse> {
                    BrowseScreenNavigator(
                        browseScreenBackStack = browseScreenBackStack,
                        currentUserData = currentUser,
                        onEpisodeNavigate = { title, link, translationGroup, serialNum, episodesCount ->
                            appNavOptions.navigateToPlayer(title, link, translationGroup, serialNum, episodesCount = episodesCount)
                        }
                    )
                }
                entry<MainNavRoute.More> {
                    MoreScreenNavigator(
                        moreScreenBackStack = moreScreenBackStack,
                        currentUser = currentUser
                    )
                }
            },
            transitionSpec = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(
                    initialAlpha = 0.1f,
                    animationSpec = tween(300)
                ) togetherWith ExitTransition.KeepUntilTransitionsFinished
            },
            popTransitionSpec = {
                EnterTransition.None togetherWith slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(targetAlpha = 0.1f, animationSpec = tween(300))
            },
            predictivePopTransitionSpec = {
                EnterTransition.None togetherWith slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(targetAlpha = 0.1f, animationSpec = tween(300))
            }
        )
    }
}

sealed class BottomNavItem(
    val title: Int,
    val selectedIconRes: Int,
    val unselectedIconRes: Int,
    val route: MainNavRoute
): NavKey {
    object Home : BottomNavItem(
        title = R.string.bottom_navigator_main,
        selectedIconRes = R.drawable.ic_selected_book,
        unselectedIconRes = R.drawable.ic_unselected_book,
        route = MainNavRoute.Home
    )

    object Browse : BottomNavItem(
        title = R.string.bottom_navigator_browse,
        selectedIconRes = R.drawable.ic_selected_browse,
        unselectedIconRes = R.drawable.ic_unselected_browse,
        route = MainNavRoute.Browse
    )

    object More : BottomNavItem(
        title = R.string.bottom_navigator_more,
        selectedIconRes = R.drawable.ic_selected_dots,
        unselectedIconRes = R.drawable.ic_unselected_dots,
        route = MainNavRoute.More
    )
}