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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

    var isInitialized by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if(!isInitialized) {
            Log.d("SideScreen", "BrowseType: $browseType")
            when (browseType) {
                is BrowseType.AnimeBrowseType -> animeBrowseViewModel.browseAnime(type = browseType)
                is BrowseType.MangaBrowseType -> mangaBrowseViewModel.browseManga(type = browseType)
            }
            isInitialized = true
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
                modifier = Modifier.padding(paddingValues).padding(horizontal = 12.dp),
                rootNavController = rootNavController,
                onLoadMore = { type ->
                    if(isInitialized) {
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
                }
            )
        }
    }
}