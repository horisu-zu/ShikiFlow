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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavOptions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CompareScreen(
    targetUser: User,
    navOptions: ProfileNavOptions
) {
    val tabs = MediaType.entries
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    Scaffold { paddingValues ->
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
                CompareScreenContent(
                    mediaType = tabs[page],
                    targetUser = targetUser,
                    onMediaItemClick = { id, mediaType ->
                        val detailsNavRoute = when(mediaType) {
                            MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                            MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                        }

                        navOptions.navigateToDetails(detailsNavRoute)
                    }
                )
            }
        }
    }
}