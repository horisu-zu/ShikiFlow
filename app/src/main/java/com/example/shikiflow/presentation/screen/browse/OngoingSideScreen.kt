package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.viewmodel.anime.BrowseViewModel
import com.example.shikiflow.utils.Resource

@Composable
fun OngoingSideScreen(
    onNavigate: (String) -> Unit,
    onScrollStateChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    browseViewModel: BrowseViewModel = hiltViewModel()
) {
    val ongoingDataState by browseViewModel.ongoingBrowseState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()

    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0 &&
            lazyListState.firstVisibleItemScrollOffset == 0
        }
    }

    LaunchedEffect(isAtTop) {
        onScrollStateChange(isAtTop)
    }

    LaunchedEffect(Unit) {
        browseViewModel.getOngoingsCalendar()
    }

    LazyColumn(
        state = lazyListState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when(ongoingDataState) {
            is Resource.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            is Resource.Success -> {
                ongoingDataState.data?.let { ongoings ->
                    ongoings.forEach { (date, animeValues) ->
                        item {
                            Text(
                                text = date,
                                modifier = Modifier.padding(horizontal = 12.dp),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        item {
                            LazyRow(
                                modifier = Modifier.height(230.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(animeValues) { anime ->
                                    BrowseGridItem(
                                        browseItem = anime,
                                        onItemClick = { id, mediaType -> onNavigate(id) },
                                        modifier = Modifier.width(120.dp)
                                    )
                                }
                            }
                        }
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
                            message = stringResource(R.string.b_oss_error),
                            buttonLabel = stringResource(id = R.string.common_retry),
                            onButtonClick = { browseViewModel.getOngoingsCalendar(isRefresh = true) }
                        )
                    }
                }
            }
        }
    }
}