package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSideScreen(
    browseType: BrowseType,
    navOptions: BrowseNavOptions,
    onBackNavigate: () -> Unit
) {
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
                        onClick = { onBackNavigate() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        if (browseType == BrowseType.AnimeBrowseType.ONGOING) {
            OngoingSideScreen(
                onNavigate = { id -> navOptions.navigateToDetails(id, MediaType.ANIME) },
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 12.dp,
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 12.dp
                )
            )
        } else {
            MainSideScreen(
                browseType = browseType,
                onMediaNavigate = { id, mediaType ->
                    navOptions.navigateToDetails(id, mediaType)
                },
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr) + 12.dp,
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr) + 12.dp),
            )
        }
    }
}