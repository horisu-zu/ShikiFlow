package com.example.shikiflow.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.presentation.screen.BottomNavItem

@Composable
fun CustomNavigationSuiteScaffold(
    selectedRoute: NavKey,
    navigationType: NavigationSuiteType,
    navItems: List<BottomNavItem>,
    onNavClick: (NavKey) -> Unit,
    navContainerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable (PaddingValues) -> Unit
) {
    val isRail = navigationType == NavigationSuiteType.NavigationRail
    val isBottomBar = navigationType == NavigationSuiteType.NavigationBar

    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        AnimatedVisibility(
            visible = isRail,
            enter = expandHorizontally(
                initialWidth = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ),
            exit = fadeOut() + shrinkHorizontally(shrinkTowards = Alignment.Start)
        ) {
            NavigationRail(
                containerColor = navContainerColor
            ) {
                navItems.forEach { navItem ->
                    val isSelected = selectedRoute == navItem.route

                    NavigationRailItem(
                        selected = isSelected,
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
                        onClick = { onNavClick(navItem.route) }
                    )
                }
            }
        }

        Scaffold(
            modifier = Modifier.weight(1f),
            contentWindowInsets = if (isRail) {
                ScaffoldDefaults.contentWindowInsets.exclude(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
            } else {
                ScaffoldDefaults.contentWindowInsets
            },
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomBar,
                    enter = expandVertically(
                        initialHeight = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                    exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
                ) {
                    NavigationBar(
                        containerColor = navContainerColor
                    ) {
                        navItems.forEach { navItem ->
                            val isSelected = selectedRoute == navItem.route

                            NavigationBarItem(
                                selected = isSelected,
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
                                onClick = { onNavClick(navItem.route) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            content(innerPadding)
        }
    }
}