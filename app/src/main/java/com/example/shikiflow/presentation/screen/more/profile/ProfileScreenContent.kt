package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.ConnectedButtonGroup
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.more.MoreNavOptions
import com.example.shikiflow.presentation.screen.more.profile.statistics.FavoritesSection
import com.example.shikiflow.presentation.viewmodel.user.ProfileStats

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreenContent(
    userData: User,
    userRateData: ProfileStats?,
    isCurrentUser: Boolean,
    moreNavOptions: MoreNavOptions,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = 16.dp
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val sectionsList = ProfileSectionType.getTabRows(
        hasEntries = !userRateData?.userMediaStats?.mediaStats.isNullOrEmpty(),
        hasFavorites = !userRateData?.favoriteCategories.isNullOrEmpty()
    )

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    val scaffoldBackgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Scaffold(
        modifier = Modifier
            .background(scaffoldBackgroundColor)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ProfileAppBar(
                userData = userData,
                scrollBehavior = scrollBehavior,
                statusBarsPadding = WindowInsets.statusBars.asPaddingValues()
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                )
                .fillMaxSize()
                .background(scaffoldBackgroundColor),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            if(sectionsList.isNotEmpty()) {
                ConnectedButtonGroup(
                    items = sectionsList,
                    selectedIndex = selectedTabIndex,
                    onItemSelection = { index ->
                        selectedTabIndex = index
                    },
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                when(sectionsList[selectedTabIndex].value) {
                    ProfileSectionType.OVERVIEW -> {
                        userRateData?.let {
                            TrackSection(
                                isCurrentUser = isCurrentUser,
                                userRateData = userRateData.userMediaStats.mediaStats,
                                onCompareClick = {
                                    moreNavOptions.navigateToCompare(userData)
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp
                                        )
                                    )
                                    .verticalScroll(rememberScrollState())
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(
                                        horizontal = horizontalPadding,
                                        vertical = 12.dp
                                    )
                            )
                        }
                    }
                    ProfileSectionType.FAVORITES -> {
                        FavoritesSection(
                            userId = userData.id,
                            favoriteCategories = userRateData?.favoriteCategories ?: emptyList(),
                            horizontalPadding = horizontalPadding,
                            onFavoriteClick = { id, category ->
                                val detailsNavRoute = when(category) {
                                    FavoriteCategory.ANIME -> DetailsNavRoute.AnimeDetails(id)
                                    FavoriteCategory.MANGA -> DetailsNavRoute.MangaDetails(id)
                                    FavoriteCategory.CHARACTER -> DetailsNavRoute.CharacterDetails(id)
                                    FavoriteCategory.STUDIO -> DetailsNavRoute.Studio(id)
                                    else -> DetailsNavRoute.Staff(id)
                                }

                                moreNavOptions.navigateToDetails(detailsNavRoute)
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(
                            shape = RoundedCornerShape(
                                topEnd = 24.dp,
                                topStart = 24.dp
                            )
                        )
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(R.string.profile_screen_empty, userData.nickname),
                        showFace = true
                    )
                }
            }
        }
    }
}