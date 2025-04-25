package com.example.shikiflow.presentation.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.shikiflow.presentation.viewmodel.user.UserViewModel

@Composable
fun MainNavigator(
    userViewModel: UserViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val currentUser by userViewModel.currentUserData.collectAsState()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavigationGraph(
            currentUser = currentUser.data,
            navController = navController,
            modifier = Modifier.padding(
                bottom = innerPadding.calculateBottomPadding()
            ) //It's an anti pattern, but Column with BottomNavBar has some weird anims during navigation
        )
    }
}