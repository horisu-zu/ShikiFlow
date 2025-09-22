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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.tracks.MediaType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSideScreen(
    browseType: BrowseType,
    navOptions: BrowseNavOptions,
    onBackNavigate: () -> Unit
) {
    var isAtTop by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = browseType.displayValueRes),
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
                    containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (browseType == BrowseType.AnimeBrowseType.ONGOING) {
            OngoingSideScreen(
                onNavigate = { id -> navOptions.navigateToDetails(id, MediaType.ANIME) },
                onScrollStateChange = { newValue -> isAtTop = newValue },
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
            )
        } else {
            MainSideScreen(
                browseType = browseType,
                onMediaNavigate = { id, mediaType ->
                    navOptions.navigateToDetails(id, mediaType)
                },
                onScrollStateChange = { newValue -> isAtTop = newValue },
                modifier = Modifier.padding(top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)),
            )
        }
    }
}