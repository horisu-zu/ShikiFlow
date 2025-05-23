package com.example.shikiflow.presentation.screen.main

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.presentation.common.CustomSearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    currentTrackMode: MainTrackMode,
    user: CurrentUserQuery.Data?,
    query: String,
    isSearchActive: Boolean,
    onQueryChange: (String) -> Unit,
    onModeChange: (MainTrackMode) -> Unit,
    onSearchToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        title = {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedContent(
                    targetState = currentTrackMode,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    modifier = Modifier.weight(1f)
                ) { trackMode ->
                    if(trackMode == MainTrackMode.ANIME) {
                        CustomSearchField(
                            query = query,
                            onQueryChange = onQueryChange,
                            isActive = isSearchActive,
                            onActiveChange = onSearchToggle
                        )
                    } else {
                        Text(
                            text = "Manga",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }

                AnimatedVisibility(
                    visible = !isSearchActive,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    AsyncImage(
                        model = user?.currentUser?.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(48.dp)
                            .clip(CircleShape)
                            .clickable { dropdownExpanded = true }
                    )
                }
            }
        },
        actions = {
            MainDropdown(
                expanded = dropdownExpanded,
                currentTrackMode = currentTrackMode,
                onModeChange = { trackMode ->
                    onModeChange(trackMode)
                },
                onDismiss = { dropdownExpanded = false }
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}