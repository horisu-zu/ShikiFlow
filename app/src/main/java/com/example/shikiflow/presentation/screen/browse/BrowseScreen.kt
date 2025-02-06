package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.presentation.viewmodel.anime.AnimeBrowseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    browseViewModel: AnimeBrowseViewModel = hiltViewModel(),
    browseNavController: NavController,
    rootNavController : NavController
) {
    val ongoingBrowseState by browseViewModel.getAnimeState(BrowseType.AnimeBrowseType.ONGOING).collectAsState()
    val isInitialized = remember {
        derivedStateOf {
            ongoingBrowseState.hasMorePages && ongoingBrowseState.items.isEmpty()
        }
    }

    LaunchedEffect(Unit) {
        if (isInitialized.value) {
            browseViewModel.browseAnime(BrowseType.AnimeBrowseType.ONGOING)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Browse",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                actions = {
                    IconButton(
                        onClick = { /**/ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    ) { paddingValues ->
        BrowseMainPage(
            ongoingBrowseState = ongoingBrowseState,
            onNavigate = { id ->
                rootNavController.navigate("animeDetailsScreen/$id")
            },
            onLoadMore = {
                browseViewModel.browseAnime(BrowseType.AnimeBrowseType.ONGOING, isLoadingMore = true)
            },
            modifier = Modifier.padding(paddingValues).padding(horizontal = 12.dp),
            browseNavController = browseNavController
        )
    }
}