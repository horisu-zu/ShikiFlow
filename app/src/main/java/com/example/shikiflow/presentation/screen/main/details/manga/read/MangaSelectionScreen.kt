package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mangadex.manga.MangaData
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.viewmodel.manga.read.MangaSelectionViewModel
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.StatusColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaSelectionScreen(
    mangaDexIds: List<String>,
    title: String,
    navOptions: MangaReadNavOptions,
    onNavigateBack: () -> Unit,
    mangaSelectionViewModel: MangaSelectionViewModel = hiltViewModel()
) {
    val mangaListState = mangaSelectionViewModel.mangaList.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(mangaDexIds) {
        mangaSelectionViewModel.fetchMangaByIds(mangaDexIds)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavigateBack() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Main"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when(val mangaList = mangaListState.value) {
                is Resource.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
                is Resource.Success -> {
                    mangaList.data?.let { mangaList ->
                        items(mangaList) { mangaItem ->
                            MangaItem(
                                mangaItem = mangaItem,
                                onClick = { mangaDexId ->
                                    navOptions.navigateToChapters(
                                        mangaDexId = mangaDexId,
                                        title = title,
                                        source = ChaptersScreenSource.MANUAL
                                    )
                                }
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorItem(
                                message = "Error: ${mangaList.message}",
                                buttonLabel = stringResource(R.string.common_retry),
                                onButtonClick = { mangaSelectionViewModel.fetchMangaByIds(mangaDexIds) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MangaItem(
    mangaItem: MangaData,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(start = 12.dp, end = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(mangaItem.data.id) },
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BaseImage(
            model = "${BuildConfig.MANGADEX_UPLOADS_URL}/covers/${mangaItem.data.id}/${mangaItem.coverUrl}",
            contentDescription = "Cover Art",
            modifier = Modifier.width(96.dp)
        )
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top)
        ) {
            Text(
                text = mangaItem.data.attributes.title.en ?: mangaItem.data.attributes.title.jaRo ?: "",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(StatusColor.getMangaDexStatusColor(mangaItem.data.attributes.status))
                )
                Text(
                    text = mangaItem.data.attributes.status.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}