package com.example.shikiflow.presentation.screen.main.details.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.shikiflow.presentation.common.TextWithDividerPlaceholder
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.presentation.screen.main.details.character.CharacterMediaSectionPlaceholder

@Composable
fun StaffScreenContentPlaceholder(
    columns: GridCells,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = columns,
        userScrollEnabled = false,
        modifier = modifier.shimmerEffect(overContent = true),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            StaffTitleSectionPlaceholder(
                modifier = Modifier.fillMaxWidth()
            )
        }

        items(4) { index ->
            StaffAttributesItemPlaceholder(
                itemIndex = index
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            FlowRow(
                maxLines = 4,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(40) { index ->
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

        item(span = { GridItemSpan(maxLineSpan) }) {
            VoiceActorRolesSectionPlaceholder()
        }

        items(
            count = 2,
            span = { GridItemSpan(maxLineSpan) }
        ) {
            CharacterMediaSectionPlaceholder()
        }
    }
}

@Composable
private fun StaffTitleSectionPlaceholder(
    modifier: Modifier = Modifier
)  {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val imageType = ImageType.Poster()

        Box(
            modifier = Modifier
                .width(imageType.width)
                .aspectRatio(imageType.aspectRatio)
                .clip(imageType.shape)
                .background(MaterialTheme.colorScheme.onSurface)
        )

        Spacer(Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(80.dp)
                        .height(MaterialTheme.typography.bodyLarge.lineHeight.value.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .background(MaterialTheme.colorScheme.onSurface)
                )

                Box(
                    modifier = Modifier
                        .width(72.dp)
                        .height(MaterialTheme.typography.bodyLarge.lineHeight.value.dp)
                        .clip(RoundedCornerShape(percent = 32))
                        .background(MaterialTheme.colorScheme.onSurface)
                )
            }

            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(MaterialTheme.typography.bodyMedium.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(40.dp)
                .clip(
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        bottomStart = 12.dp,
                        topEnd = 4.dp,
                        bottomEnd = 4.dp,
                    )
                )
                .background(MaterialTheme.colorScheme.onSurface)
        )
    }
}

@Composable
fun StaffAttributesItemPlaceholder(
    itemIndex: Int,
    modifier: Modifier = Modifier,
    maxValue: Int = 4
) {
    val indexValue = itemIndex % 4 + 1

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(
                    width = when (indexValue <= maxValue / 2) {
                        true -> 48.dp + indexValue * 12.dp
                        false -> 72.dp - indexValue * 8.dp
                    }
                )
                .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp + 12.dp)
                .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
        )

        Box(
            modifier = Modifier
                .width(96.dp - indexValue * 8.dp)
                .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp + 12.dp)
                .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(percent = 32))
        )
    }
}

@Composable
fun VoiceActorRolesSectionPlaceholder(
    modifier: Modifier = Modifier,
    imageType: ImageType = ImageType.Poster(
        shape = RoundedCornerShape(percent = 16)
    )
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextWithDividerPlaceholder()

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(percent = 24))
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        }

        LazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(12.dp)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(count = 16) {
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