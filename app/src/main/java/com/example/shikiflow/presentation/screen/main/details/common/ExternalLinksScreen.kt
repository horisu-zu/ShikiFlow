package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.domain.model.common.ExternalLink
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.ExternalLinksViewModel
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent.openUrlCustomTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalLinksScreen(
    mediaId: String,
    mediaType: MediaType,
    navOptions: MediaNavOptions,
    externalLinksViewModel: ExternalLinksViewModel
) {
    val context = LocalContext.current
    val externalLinks = externalLinksViewModel.externalLinks.collectAsStateWithLifecycle()

    LaunchedEffect(mediaId, mediaType) {
        externalLinksViewModel.getExternalLinks(mediaId, mediaType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "External Links",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navOptions.navigateBack() }
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(
                top = innerPadding.calculateTopPadding(),
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
            ).padding(horizontal = 12.dp).clip(RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            when(val links = externalLinks.value) {
                is Resource.Error -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize().padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Error: ${links.message}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
                is Resource.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
                is Resource.Success -> {
                    items(links.data?.size ?: 0) { index ->
                        links.data?.let { externalLinks ->
                            LinkItem(
                                link = externalLinks[index],
                                onLinkClick = { url ->
                                    openUrlCustomTab(context, url)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LinkItem(
    link: ExternalLink,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable { onLinkClick(link.url) }
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Text(
            text = link.kind.replace("_", " ").split(" ")
                .joinToString(" ") { it.replaceFirstChar { it.uppercaseChar() } },
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = link.url,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(0.75f)
            )
        )
    }
}