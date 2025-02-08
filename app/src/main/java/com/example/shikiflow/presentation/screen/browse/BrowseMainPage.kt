package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.shikiflow.data.anime.BrowseState
import com.example.shikiflow.data.anime.toBrowseAnime

@Composable
fun BrowseMainPage(
    ongoingBrowseState: BrowseState.AnimeBrowseState,
    onNavigate: (String) -> Unit,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    browseNavController: NavController
) {
    if(ongoingBrowseState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val ongoingList = ongoingBrowseState.items

            item(
                span = { GridItemSpan(3) }
            ) {
                NavigationSection(
                    browseNavController = browseNavController
                )
            }

            items(ongoingList.size) { index ->
                BrowseItem(
                    browseItem = ongoingList[index].toBrowseAnime(),
                    onItemClick = onNavigate
                )
            }

            if (ongoingBrowseState.hasMorePages) {
                item(
                    span = { GridItemSpan(3) }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                    onLoadMore()
                }
            }
        }
    }
}