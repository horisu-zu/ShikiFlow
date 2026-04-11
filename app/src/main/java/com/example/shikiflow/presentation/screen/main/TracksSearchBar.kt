package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.CustomSearchField
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.iconResource
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracksSearchBar(
    scrollBehavior: TopAppBarScrollBehavior,
    currentTrackMode: MediaType,
    query: String,
    isSearchActive: Boolean,
    onModeChange: (MediaType) -> Unit,
    onQueryChange: (String) -> Unit,
    onSearchToggle: (Boolean) -> Unit,
    onExitSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if(scrollBehavior.state.collapsedFraction >= 1f || isSearchActive) {
        MaterialTheme.colorScheme.surfaceContainer
    } else { MaterialTheme.colorScheme.background }

    val selectorBackgroundColor = when(isSearchActive) {
        true -> MaterialTheme.colorScheme.background
        false -> MaterialTheme.colorScheme.surfaceContainer
    }

    Column {
        TopAppBar(
            modifier = modifier,
            scrollBehavior = scrollBehavior,
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomSearchField(
                        query = query,
                        label = stringResource(R.string.tracks_page_search),
                        onQueryChange = onQueryChange,
                        isActive = isSearchActive,
                        onActiveChange = onSearchToggle,
                        onExitSearch = onExitSearch,
                        modifier = Modifier.weight(1f)
                    )

                    TracksTypeSelector(
                        currentType = currentTrackMode,
                        onModeChange = onModeChange,
                        backgroundColor = selectorBackgroundColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(selectorBackgroundColor)
                            .padding(all = 8.dp)
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = containerColor,
                scrolledContainerColor = containerColor
            )
        )

        if(isSearchActive && scrollBehavior.state.collapsedFraction < 1f) {
            HorizontalDivider()
        }
    }
}

@Composable
private fun TracksTypeSelector(
    currentType: MediaType,
    onModeChange: (MediaType) -> Unit,
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MediaType.entries.forEach { mediaType ->
            val isCurrent = currentType == mediaType

            mediaType.iconResource().toIcon(
                tint = when(isCurrent) {
                    true -> MaterialTheme.colorScheme.onPrimary
                    false -> MaterialTheme.colorScheme.onBackground
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onModeChange(mediaType) }
                    .background(
                        color = when(isCurrent) {
                            true -> MaterialTheme.colorScheme.primary
                            false -> backgroundColor
                        }
                    )
                    .padding(all = 6.dp)
            )
        }
    }
}