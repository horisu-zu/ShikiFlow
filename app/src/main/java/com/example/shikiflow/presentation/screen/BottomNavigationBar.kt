package com.example.shikiflow.presentation.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shikiflow.R
import com.example.shikiflow.presentation.screen.main.MainScreen

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Browse,
        BottomNavItem.More
    )
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(
                            id = if (isSelected) item.selectedIconRes else item.unselectedIconRes
                        ),
                        contentDescription = item.title,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController, startDestination = BottomNavItem.Home.route, modifier = modifier) {
        composable(BottomNavItem.Home.route) { MainScreen() }
        composable(BottomNavItem.Browse.route) { TestScreen() }
        composable(BottomNavItem.More.route) { /*ProfileScreen()*/ }
    }
}

sealed class BottomNavItem(
    var title: String,
    @DrawableRes var selectedIconRes: Int,
    @DrawableRes var unselectedIconRes: Int,
    var route: String
) {
    object Home : BottomNavItem("Main", R.drawable.ic_selected_book, R.drawable.ic_unselected_book, "home")
    object Browse : BottomNavItem("Browse", R.drawable.ic_selected_browse, R.drawable.ic_unselected_browse, "search")
    object More : BottomNavItem("More", R.drawable.ic_selected_dots, R.drawable.ic_unselected_dots, "profile")
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}