package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.times
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mangadex.manga.MangaData
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ColorMapper.getMangaDexStatusColor
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.presentation.viewmodel.manga.read.selection.MangaSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaSelectionScreen(
    mangaDexIds: List<String>,
    title: String,
    navOptions: MangaReadNavOptions,
    mangaSelectionViewModel: MangaSelectionViewModel = hiltViewModel()
) {
    val uiState by mangaSelectionViewModel.uiState.collectAsStateWithLifecycle()

    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 && lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(mangaDexIds) {
        mangaSelectionViewModel.setMangaDexIds(mangaDexIds)
    }

    Scaffold(
        topBar = {
            Column {
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
                            onClick = { navOptions.navigateBack() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Main"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                            else MaterialTheme.colorScheme.surfaceContainer
                    )
                )
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 8.dp,
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if(uiState.isLoading) {
                items(3) { index ->
                    MangaItemPlaceholder(
                        itemIndex = index,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else if(uiState.errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = uiState.errorMessage ?: stringResource(id = R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { mangaSelectionViewModel.onRefresh() }
                        )
                    }
                }
            } else {
                items(uiState.mangaList) { mangaItem ->
                    MangaItem(
                        mangaItem = mangaItem,
                        onClick = { mangaDexId ->
                            navOptions.navigateToChapters(
                                mangaDexId = mangaDexId,
                                title = title
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun MangaItem(
    mangaItem: MangaData,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(mangaItem.id) },
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BaseImage(
            model = mangaItem.coverUrl,
            contentDescription = "Cover Art",
            modifier = Modifier.width(96.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top)
        ) {
            Text(
                text = mangaItem.title,
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
                        .size(12.dp)
                        .clip(RoundedCornerShape(percent = 24))
                        .background(color = getMangaDexStatusColor(mangaItem.status))
                )

                Text(
                    text = mangaItem.status.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun MangaItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier
) {
    val imageType = ImageType.Poster()
    val indexValue = itemIndex % 4 + 1

    Row(
        modifier = modifier.shimmerEffect(overContent = true),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .width(imageType.width)
                .aspectRatio(imageType.aspectRatio)
                .clip(imageType.shape)
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Box(
                modifier = Modifier
                    .width(80.dp + indexValue * 8.dp)
                    .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .background(MaterialTheme.colorScheme.onSurface)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(RoundedCornerShape(percent = 24))
                        .background(MaterialTheme.colorScheme.onSurface)
                )

                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            }
        }
    }
}