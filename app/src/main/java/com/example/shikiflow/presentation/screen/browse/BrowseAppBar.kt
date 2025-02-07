package com.example.shikiflow.presentation.screen.browse

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.shikiflow.presentation.common.CustomSearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseAppBar(
    title: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            AnimatedContent(
                targetState = isSearchActive,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { isSearch ->
                if (isSearch) {
                    CustomSearchField(
                        query = searchQuery,
                        onQueryChange = onSearchQueryChange,
                        isActive = isSearchActive,
                        onActiveChange = onSearchActiveChange,
                        modifier = Modifier.fillMaxWidth()
                    )
                } else {
                    Text(title)
                }
            }
        },
        actions = {
            if(!isSearchActive) {
                IconButton(
                    onClick = { onSearchActiveChange(true) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
    )
}
