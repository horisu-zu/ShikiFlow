package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.WindowSize
import com.example.shikiflow.presentation.common.CardItemPlaceholder
import com.example.shikiflow.presentation.common.TextWithDividerPlaceholder
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.presentation.screen.main.details.anime.ScoreItemPlaceholder
import com.example.shikiflow.presentation.screen.main.details.anime.ShortInfoItemPlaceholder

@Composable
fun MediaDetailsContentPlaceholder(
   mediaType: MediaType,
   modifier: Modifier = Modifier,
   horizontalPadding: Dp = 12.dp
) {
    LazyColumn(
        //userScrollEnabled = false,
        modifier = modifier.shimmerEffect(overContent = true),
        contentPadding = PaddingValues(
            start = horizontalPadding,
            end = horizontalPadding,
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        item {
            MediaDetailsHeaderPlaceholder(
                mediaType = mediaType,
                horizontalPadding = horizontalPadding,
                modifier = Modifier
                    .ignoreHorizontalParentPadding(horizontalPadding)
                    .fillMaxWidth()
            )
        }

        //Description
        item {
            FlowRow(
                maxLines = 8,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(64) { index ->
                    val indexValue = index % 8 + 1
                    val itemWidth = when {
                        indexValue <= 3 -> 36.dp + indexValue * 8.dp
                        indexValue <= 6 -> 80.dp - indexValue * 4.dp
                        else -> 32.dp + indexValue * 6.dp
                    }

                    Box(
                        modifier = Modifier
                            .width(itemWidth)
                            .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                            .clip(RoundedCornerShape(percent = 32))
                            .background(MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }

        //Genres and Tags
        items(count = 2) { index ->
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextWithDividerPlaceholder(
                    textWidth = 64.dp - index * 16.dp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
                ) {
                    repeat(3 * (index + 1)) { index ->
                        val indexValue = index % 3 + 1

                        CardItemPlaceholder(
                            modifier = Modifier.width(48.dp + indexValue * 6.dp)
                        )
                    }
                }
            }
        }

        //Characters
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextWithDividerPlaceholder()

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(percent = 24))
                            .background(MaterialTheme.colorScheme.onSurface)
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .ignoreHorizontalParentPadding(horizontalPadding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(count = 16) { index ->
                        CharacterCardPlaceholder(
                            itemIndex = index,
                            imageType = ImageType.Custom(
                                width = 96.dp,
                                aspectRatio = 2f / 2.85f,
                                shape = RoundedCornerShape(percent = 16)
                            )
                        )
                    }
                }
            }
        }

        //Related
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextWithDividerPlaceholder(
                        textWidth = 96.dp
                    )

                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(percent = 24))
                            .background(MaterialTheme.colorScheme.onSurface)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(percent = 24))
                            .background(MaterialTheme.colorScheme.onSurface)
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(3) { index ->
                        RelatedItemPlaceholder(
                            itemIndex = index
                        )
                    }
                }
            }
        }

        //Screenshots
        if (mediaType == MediaType.ANIME) {
            item {
                val imageType = ImageType.Screenshot()

                LazyRow(
                    modifier = Modifier
                        .ignoreHorizontalParentPadding(horizontalPadding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(12) {
                        Box(
                            modifier = Modifier
                                .width(imageType.width)
                                .aspectRatio(imageType.aspectRatio)
                                .clip(imageType.shape)
                                .background(MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }
        }

        //Staff
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextWithDividerPlaceholder()

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(percent = 24))
                            .background(MaterialTheme.colorScheme.onSurface)
                    )
                }

                LazyRow(
                    modifier = Modifier
                        .ignoreHorizontalParentPadding(horizontalPadding)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(count = 16) { index ->
                        CharacterCardPlaceholder(
                            itemIndex = index,
                            imageType = ImageType.Custom(
                                width = 96.dp,
                                aspectRatio = 2f / 2.85f,
                                shape = RoundedCornerShape(percent = 16)
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaDetailsHeaderPlaceholder(
    mediaType: MediaType,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    val imageType = ImageType.Poster()
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    val windowSize by remember(windowSizeClass) {
        derivedStateOf {
            WindowSize.from(windowSizeClass)
        }
    }

    Row(
        modifier = modifier
            .aspectRatio(
                ratio = when (windowSize) {
                    WindowSize.COMPACT -> imageType.aspectRatio
                    WindowSize.MEDIUM -> 24f / 9f
                    WindowSize.EXPANDED -> 32f / 9f
                }
            )
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        if (windowSize != WindowSize.COMPACT) {
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .aspectRatio(imageType.aspectRatio)
                    .clip(imageType.shape)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        }

        Column {
            ScoreItemPlaceholder(
                modifier = Modifier.padding(vertical = 4.dp)
            )

            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(MaterialTheme.typography.headlineSmall.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .background(MaterialTheme.colorScheme.onSurface)
            )

            Row(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
            ) {
                repeat(3) { index ->
                    ShortInfoItemPlaceholder(
                        itemIndex = index
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.onSurface)
                )

                repeat(
                    times = when (mediaType) {
                        MediaType.ANIME -> 1
                        MediaType.MANGA -> 2
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.onSurface)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MediaDetailsContentPlaceholderPreview() {
    Scaffold { paddingValues ->
        MediaDetailsContentPlaceholder(
            mediaType = MediaType.ANIME,
            modifier = Modifier.padding(paddingValues)
        )
    }
}