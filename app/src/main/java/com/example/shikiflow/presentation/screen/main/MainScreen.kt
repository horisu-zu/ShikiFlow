package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainNavController: NavController,
    currentUser: CurrentUserQuery.Data?,
    searchViewModel: SearchViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState { 6 }
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        state = topAppBarState,
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessVeryLow
        )
    )
    val screenState by searchViewModel.screenState.collectAsState()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MainAppBar(
                scrollBehavior = scrollBehavior,
                user = currentUser,
                query = screenState.query,
                isSearchActive = screenState.isSearchActive,
                onQueryChange = searchViewModel::onQueryChange,
                onSearchToggle = searchViewModel::onSearchActiveChange,
                onAvatarClick = { /**/ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top
        ) {
            Crossfade(targetState = screenState.isSearchActive) { isSearchActive ->
                if (isSearchActive) {
                    SearchPage()
                } else {
                    MainPage(
                        mainNavController = mainNavController,
                        pagerState = pagerState
                    )
                }
            }
        }
    }
}