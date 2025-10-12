package com.example.shikiflow.presentation.screen.browse

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.CustomSearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseAppBar(
    title: String,
    searchQuery: String,
    isAtTop: Boolean,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onSearchActiveChange: (Boolean) -> Unit,
    onExitSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        TopAppBar(
            title = {
                AnimatedContent(targetState = isSearchActive) { isSearch ->
                    if (isSearch) {
                        CustomSearchField(
                            query = searchQuery,
                            label = stringResource(R.string.browse_page_search),
                            onQueryChange = onSearchQueryChange,
                            onActiveChange = onSearchActiveChange,
                            onExitSearch = onExitSearch,
                            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                            activeContainerColor = MaterialTheme.colorScheme.surface,
                            inactiveContainerColor = MaterialTheme.colorScheme.background
                        )
                    } else { Text(title) }
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
                containerColor = if(isAtTop && !isSearchActive) MaterialTheme.colorScheme.background
                    else MaterialTheme.colorScheme.surface
            ),
            modifier = modifier
        )
        if(!isAtTop || isSearchActive) HorizontalDivider()
    }
}
