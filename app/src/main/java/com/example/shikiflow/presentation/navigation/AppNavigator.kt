package com.example.shikiflow.presentation.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shikiflow.data.auth.AuthState
import com.example.shikiflow.presentation.auth.AuthScreen
import com.example.shikiflow.presentation.screen.MainNavigator
import com.example.shikiflow.presentation.viewmodel.AuthViewModel

@Composable
fun AppNavigator(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            val authState by viewModel.authState.collectAsState()

            LaunchedEffect(authState) {
                when(authState) {
                    AuthState.Success -> {
                        Log.d("AppNavigator", "Navigating to main")
                        navController.navigate("main") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    AuthState.Initial -> {
                        Log.d("AppNavigator", "Navigating to auth")
                        navController.navigate("auth") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                    else -> {}
                }
            }
        }
        composable(route = "auth") {
            AuthScreen(navController = navController)
        }
        composable(route = "main") {
            MainNavigator(parentNavController = navController)
        }
    }
}