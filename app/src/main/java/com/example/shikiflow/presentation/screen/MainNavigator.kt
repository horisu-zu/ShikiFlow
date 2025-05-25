package com.example.shikiflow.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation3.runtime.rememberNavBackStack
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel
import com.example.shikiflow.utils.AppSettingsManager

@Composable
fun MainNavigator(
    appSettingsManager: AppSettingsManager,
    userViewModel: UserViewModel = hiltViewModel()
) {
    val mainBackstack = rememberNavBackStack(MainNavRoute.Home)
    //val mainBackStack = remember { mutableStateListOf<MainNavRoute>(MainNavRoute.Home) }
    val currentUser by userViewModel.currentUserData.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar(
            currentRoute = mainBackstack.lastOrNull() ?: MainNavRoute.Home,
            onNavigate = { route ->
                mainBackstack.add(route)
            }
        ) }
    ) { innerPadding ->
        NavigationGraph(
            appSettingsManager = appSettingsManager,
            currentUser = currentUser.data,
            mainBackstack = mainBackstack,
            modifier = Modifier.padding(
                bottom = innerPadding.calculateBottomPadding()
            ) //It's an anti pattern, but Column with BottomNavBar has some weird anims during navigation
        )
    }
}