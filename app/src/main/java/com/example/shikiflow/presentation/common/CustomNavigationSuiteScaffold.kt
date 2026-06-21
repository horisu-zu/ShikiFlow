package com.example.shikiflow.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.shikiflow.presentation.navigation.BottomNavItem
import com.example.shikiflow.utils.toIcon

@Composable
fun CustomNavigationSuiteScaffold(
    selectedRoute: NavKey,
    navigationType: NavigationSuiteType,
    navItems: List<BottomNavItem>,
    onNavClick: (BottomNavItem) -> Unit,
    navContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    content: @Composable (PaddingValues) -> Unit
) {
    val isRail = remember(navigationType) {
        navigationType == NavigationSuiteType.NavigationRail ||
        navigationType == NavigationSuiteType.WideNavigationRailCollapsed
    }
    val isBottomBar = remember(navigationType) {
        navigationType == NavigationSuiteType.ShortNavigationBarCompact ||
        navigationType == NavigationSuiteType.ShortNavigationBarMedium
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AnimatedVisibility(
            visible = isRail,
            enter = expandHorizontally(
                initialWidth = { -it },
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = shrinkHorizontally(
                shrinkTowards = Alignment.Start,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ) {
            WideNavigationRail(
                colors = WideNavigationRailDefaults.colors(
                    containerColor = navContainerColor
                ),
                arrangement = Arrangement.Top,
                contentPadding = PaddingValues(0.dp)
            ) {
                navItems.forEach { navItem ->
                    val isSelected = selectedRoute == navItem.route
                    val railExpanded = navigationType == NavigationSuiteType.WideNavigationRailExpanded

                    WideNavigationRailItem(
                        railExpanded = railExpanded,
                        selected = isSelected,
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
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        },
                        onClick = { onNavClick(navItem) }
                    )
                }
            }
        }

        Scaffold(
            modifier = Modifier.weight(1f),
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
                .apply {
                    if(isRail) {
                        exclude(WindowInsets.safeDrawing.only(WindowInsetsSides.Start))
                    }
                },
            bottomBar = {
                AnimatedVisibility(
                    visible = isBottomBar,
                    enter = expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                    exit = shrinkVertically(
                        shrinkTowards = Alignment.Bottom,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    ShortNavigationBar(
                        containerColor = navContainerColor
                    ) {
                        navItems.forEach { navItem ->
                            val isSelected = selectedRoute == navItem.route

                            ShortNavigationBarItem(
                                selected = isSelected,
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
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                },
                                onClick = { onNavClick(navItem) }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            content(paddingValues)
        }
    }
}