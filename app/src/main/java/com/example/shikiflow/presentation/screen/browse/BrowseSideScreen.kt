package com.example.shikiflow.presentation.screen.browse

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shikiflow.data.anime.BrowseState
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.presentation.viewmodel.anime.AnimeBrowseViewModel
import com.example.shikiflow.presentation.viewmodel.manga.MangaBrowseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSideScreen(
    browseType: BrowseType,
    rootNavController: NavController,
    browseNavController: NavController,
    animeBrowseViewModel: AnimeBrowseViewModel = hiltViewModel(),
    mangaBrowseViewModel: MangaBrowseViewModel = hiltViewModel()
) {
    val state = when(browseType) {
        is BrowseType.AnimeBrowseType -> animeBrowseViewModel.getAnimeState(browseType).collectAsState()
        is BrowseType.MangaBrowseType -> mangaBrowseViewModel.getMangaState(browseType).collectAsState()
    }.value

    val items = when (state) {
        is BrowseState.AnimeBrowseState -> state.items
        is BrowseState.MangaBrowseState -> state.items
    }
    val isInitialized = remember {
        derivedStateOf {
            state.hasMorePages && items.isNotEmpty()
        }
    }

    LaunchedEffect(Unit) {
        if(!isInitialized.value) {
            Log.d("SideScreen", "BrowseType: $browseType")
            when(browseType) {
                is BrowseType.AnimeBrowseType -> animeBrowseViewModel.browseAnime(type = browseType)
                is BrowseType.MangaBrowseType -> mangaBrowseViewModel.browseManga(type = browseType)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = browseType.toString().lowercase().replaceFirstChar { it.uppercase() }
                            .replace("_", " "),
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { browseNavController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
        if(state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (browseType == BrowseType.AnimeBrowseType.ONGOING && state is BrowseState.AnimeBrowseState) {
            OngoingSideScreen(
                state = state,
                modifier = Modifier.padding(paddingValues).padding(horizontal = 12.dp),
                rootNavController = rootNavController
            )
        } else {
            MainSideScreen(
                state = state,
                browseType = browseType,
                modifier = Modifier.padding(paddingValues).padding(horizontal = 12.dp),
                rootNavController = rootNavController,
                onLoadMore = { type ->
                    when(type) {
                        MediaType.ANIME -> {
                            animeBrowseViewModel.browseAnime(
                                type = browseType,
                                isLoadingMore = true
                            )
                        }
                        MediaType.MANGA -> {
                            mangaBrowseViewModel.browseManga(
                                type = browseType,
                                isLoadingMore = true
                            )
                        }
                    }
                }
            )
        }
    }
}