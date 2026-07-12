package com.example.shikiflow.presentation.screen.more.compare

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavOptions
import com.example.shikiflow.presentation.viewmodel.user.compare.CompareScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CompareScreen(
    targetUser: User,
    navOptions: ProfileNavOptions,
    compareScreenViewModel: CompareScreenViewModel = hiltViewModel()
) {
    val tabs = MediaType.entries
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()
    val showBottomSheet = remember { mutableStateOf(false) }

    val authType by compareScreenViewModel.authType.collectAsStateWithLifecycle()
    val uiState by compareScreenViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showBottomSheet.value = true },
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_sort),
                    contentDescription = "Show Sort Bottom Sheet"
                )
            }
        }
    ) { paddingValues ->
        Column {
            CompareTabRow(
                tabs = tabs.map { it.displayValue() },
                selectedTab = pagerState.currentPage,
                containerColor = Color.Transparent,
                onTabSelected = { pageIndex ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = pageIndex,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    }
                },
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(top = paddingValues.calculateTopPadding())
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                LaunchedEffect(page) {
                    compareScreenViewModel.setData(targetUser.id, tabs[page])
                }

                CompareScreenContent(
                    mediaType = tabs[page],
                    targetUser = targetUser,
                    uiState = uiState,
                    onMediaItemClick = { id, mediaType ->
                        val detailsNavRoute = when(mediaType) {
                            MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                            MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                        }

                        navOptions.navigateToDetails(detailsNavRoute)
                    },
                    onRefresh = { compareScreenViewModel.onRefresh(tabs[page]) }
                )
            }
        }

        if (showBottomSheet.value) {
            authType?.let { authType ->
                CompareSortBottomSheet(
                    authType = authType,
                    currentUserName = uiState.currentUser?.nickname ?: "",
                    targetUserName = targetUser.nickname,
                    scoreFormat = uiState.scoreFormat ?: ScoreFormat.POINT_10,
                    filters = uiState.filters,
                    onFiltersChange = { filters ->
                        compareScreenViewModel.setFilters(filters)
                    },
                    onDismiss = { showBottomSheet.value = false }
                )
            }
        }
    }
}