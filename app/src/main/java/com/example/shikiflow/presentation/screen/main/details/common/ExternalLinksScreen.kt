package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.ExternalLinksViewModel
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent.openUrlCustomTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExternalLinksScreen(
    mediaId: Int,
    mediaType: MediaType,
    navOptions: MediaNavOptions,
    externalLinksViewModel: ExternalLinksViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val externalLinks by externalLinksViewModel.externalLinks.collectAsStateWithLifecycle()

    LaunchedEffect(mediaId, mediaType) {
        externalLinksViewModel.getExternalLinks(mediaId, mediaType)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.external_links_label),
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
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
                .clip(RoundedCornerShape(12.dp)),
            contentPadding = PaddingValues(
                all = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
        ) {
            when(externalLinks) {
                is Resource.Error -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorItem(
                                message = externalLinks.message ?: stringResource(R.string.common_error),
                                buttonLabel = stringResource(R.string.common_retry),
                                onButtonClick = {
                                    externalLinksViewModel.getExternalLinks(mediaId, mediaType)
                                }
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
                    externalLinks.data?.let { links ->
                        items(links) { item ->
                            LinkItem(
                                link = item,
                                onLinkClick = { url ->
                                    openUrlCustomTab(context, url)
                                },
                                modifier = Modifier.fillMaxWidth()
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
    link: ExternalLinkData,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onLinkClick(link.url) }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(all = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            link.icon?.let { siteIcon ->
                BaseImage(
                    model = siteIcon,
                    imageType = ImageType.Custom(
                        defaultAspectRatio = 1f,
                        defaultWidth = 24.dp,
                        defaultClip = RoundedCornerShape(0.dp)
                    )
                )
            } ?: Icon(
                painter = painterResource(id = R.drawable.ic_round_link),
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Site Icon",
                modifier = Modifier.size(24.dp)
            )
        }
        Text(
            text = link.siteName,
            style = MaterialTheme.typography.bodyLarge
        )
        /*Column(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Text(
                text = link.siteName,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = link.url,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(0.75f)
                )
            )
        }*/
    }
}