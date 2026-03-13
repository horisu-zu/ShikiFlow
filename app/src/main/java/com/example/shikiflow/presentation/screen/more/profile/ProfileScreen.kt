package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.ConnectedButtonGroup
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.more.MoreNavOptions
import com.example.shikiflow.presentation.screen.more.profile.statistics.FavoritesSection
import com.example.shikiflow.presentation.viewmodel.user.UserRateViewModel
import com.example.shikiflow.presentation.viewmodel.user.UserRatesUiState

@Composable
fun ProfileScreen(
    currentUserId: String?,
    userData: User?,
    moreNavOptions: MoreNavOptions,
    userRateViewModel: UserRateViewModel = hiltViewModel()
) {
    val userRatesUiState by userRateViewModel.userRatesUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        userData?.id?.let { userId ->
            userRateViewModel.loadUserRates(userId)
        }
    }

    userData?.let {
        ProfileScreenContent(
            userData = userData,
            userRatesUiState = userRatesUiState,
            isCurrentUser = currentUserId == userData.id,
            moreNavOptions = moreNavOptions,
            onRefresh = { userRateViewModel.loadUserRates(userData.id) },
            modifier = Modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreenContent(
    userData: User,
    userRatesUiState: UserRatesUiState,
    isCurrentUser: Boolean,
    moreNavOptions: MoreNavOptions,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val horizontalPadding = 16.dp
    var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
    val sectionsList = ProfileSectionType.getTabRows(
        hasEntries = userRatesUiState.userMediaStats.mediaStats.isNotEmpty(),
        hasFavorites = userRatesUiState.favoriteCategories.isNotEmpty()
    )

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ProfileAppBar(
                userData = userData,
                scrollBehavior = scrollBehavior,
                statusBarsPadding = WindowInsets.statusBars.asPaddingValues(),
                backgroundColor = backgroundColor
            )
        }
    ) { paddingValues ->
        if(userRatesUiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(userRatesUiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = userRatesUiState.errorMessage,
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { onRefresh() }
                )
            }
        } else {
            Column(
                modifier = modifier
                    .padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                    )
                    .fillMaxSize()
            ) {
                if(sectionsList.isNotEmpty()) {
                    ConnectedButtonGroup(
                        items = sectionsList,
                        selectedIndex = selectedTabIndex,
                        onItemSelection = { index ->
                            selectedTabIndex = index
                        },
                        modifier = Modifier
                            .background(backgroundColor)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                    when(sectionsList[selectedTabIndex].value) {
                        ProfileSectionType.OVERVIEW -> {
                            TrackSection(
                                isCurrentUser = isCurrentUser,
                                userRateData = userRatesUiState.userMediaStats.mediaStats,
                                onCompareClick = {
                                    moreNavOptions.navigateToCompare(userData)
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(backgroundColor)
                                    .clip(
                                        shape = RoundedCornerShape(
                                            topStart = 24.dp,
                                            topEnd = 24.dp
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
                        ProfileSectionType.FAVORITES -> {
                            FavoritesSection(
                                userId = userData.id,
                                favoriteCategories = userRatesUiState.favoriteCategories,
                                horizontalPadding = horizontalPadding,
                                backgroundColor = backgroundColor,
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
                            ),
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
}