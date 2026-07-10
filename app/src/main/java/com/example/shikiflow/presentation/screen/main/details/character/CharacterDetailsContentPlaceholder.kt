package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.presentation.screen.main.details.staff.StaffAttributesItemPlaceholder
import com.example.shikiflow.presentation.screen.main.details.staff.VoiceActorRolesSectionPlaceholder

@Composable
fun CharacterDetailsContentPlaceholder(
    columns: GridCells,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = columns,
        userScrollEnabled = false,
        modifier = modifier.shimmerEffect(overContent = true),
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            CharacterTitleSectionPlaceholder()
        }

        items(4) { index ->
            StaffAttributesItemPlaceholder(
                itemIndex = index
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
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