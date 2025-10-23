package com.example.shikiflow.presentation.screen.more.compare

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.more.MoreNavOptions
import com.example.shikiflow.presentation.viewmodel.user.CompareScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CompareScreen(
    currentUser: User,
    targetUser: User,
    moreNavOptions: MoreNavOptions,
    compareScreenViewModel: CompareScreenViewModel = hiltViewModel()
) {
    val tabs = linkedMapOf(
        MediaType.ANIME to stringResource(R.string.main_track_mode_anime),
        MediaType.MANGA to stringResource(R.string.main_track_mode_manga)
    )
    val pagerState = rememberPagerState { tabs.size }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.compare_label)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { moreNavOptions.navigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Main"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateLeftPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateRightPadding(LayoutDirection.Ltr)
                )
        ) {
            CompareTabRow(
                tabs = tabs.map { it.value },
                selectedTab = pagerState.currentPage,
                containerColor = MaterialTheme.colorScheme.surface,
                onTabSelected = { pageIndex ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(
                            page = pageIndex,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                }
            )
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                CompareScreenContent(
                    mediaType = tabs.map { it.key }[page],
                    currentUser = currentUser,
                    targetUser = targetUser,
                    compareScreenViewModel = compareScreenViewModel
                )
            }
        }
    }
}