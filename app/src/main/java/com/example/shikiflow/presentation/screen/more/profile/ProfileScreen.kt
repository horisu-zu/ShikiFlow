package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.ConnectedButtonGroup
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute.*
import com.example.shikiflow.presentation.screen.more.profile.favorites.FavoritesSection
import com.example.shikiflow.presentation.screen.more.profile.activity.UserActivitySection
import com.example.shikiflow.presentation.screen.more.profile.social.SocialSection
import com.example.shikiflow.presentation.viewmodel.user.profile.ProfileViewModel
import com.example.shikiflow.presentation.viewmodel.user.profile.ProfileUiState

@Composable
fun ProfileScreen(
    userData: User?,
    navOptions: ProfileNavOptions,
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()
    val user = userData ?: uiState.currentUser

    LaunchedEffect(user) {
        user?.id?.let { userId ->
            profileViewModel.setUserId(userId)
        }
    }

    user?.let {
        ProfileScreenContent(
            userData = user,
            uiState = uiState,
            isCurrentUser = uiState.currentUser?.id == user.id,
            navOptions = navOptions,
            onRefresh = { profileViewModel.onRefresh() },
            modifier = Modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProfileScreenContent(
    userData: User,
    uiState: ProfileUiState,
    isCurrentUser: Boolean,
    navOptions: ProfileNavOptions,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        snapAnimationSpec = MaterialTheme.motionScheme.defaultSpatialSpec()
    )
    val backgroundColor = MaterialTheme.colorScheme.background

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            ProfileAppBar(
                userData = userData,
                isCurrentUser = isCurrentUser,
                scrollBehavior = scrollBehavior,
                statusBarsPadding = WindowInsets.statusBars.asPaddingValues(),
                backgroundColor = backgroundColor,
                navOptions = navOptions
            )
        }
    ) { paddingValues ->
        if(uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(uiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = uiState.errorMessage,
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { onRefresh() }
                )
            }
        } else {
            val horizontalPadding = 12.dp
            var selectedTabIndex by rememberSaveable { mutableIntStateOf(0) }
            val sectionsList = ProfileSectionType.getTabRows(uiState.userStatsCategories)

            Column(
                modifier = modifier
                    .padding(top = paddingValues.calculateTopPadding())
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
                        ProfileSectionType.USER_STATS -> {
                            UserStatsSection(
                                userData = userData,
                                typesList = uiState.userStatsCategories.scoreMediaTypes,
                                isCurrentUser = isCurrentUser,
                                isRefreshEnabled = scrollBehavior.state.collapsedFraction == 0f,
                                horizontalPadding = horizontalPadding,
                                navOptions = navOptions
                            )
                        }
                        ProfileSectionType.ACTIVITY -> {
                            UserActivitySection(
                                userId = userData.id,
                                isRefreshEnabled = scrollBehavior.state.collapsedFraction == 0f,
                                horizontalPadding = horizontalPadding,
                                navOptions = navOptions
                            )
                        }
                        ProfileSectionType.SOCIAL -> {
                            SocialSection(
                                userId = userData.id,
                                socialCategories = uiState.userStatsCategories.socialCategories,
                                horizontalPadding = horizontalPadding,
                                navOptions = navOptions
                            )
                        }
                        ProfileSectionType.FAVORITES -> {
                            FavoritesSection(
                                userId = userData.id,
                                favoriteCategories = uiState.userStatsCategories.favoriteCategories,
                                isRefreshEnabled = scrollBehavior.state.collapsedFraction == 0f,
                                horizontalPadding = horizontalPadding,
                                onFavoriteClick = { category, id ->
                                    val detailsNavRoute = when(category) {
                                        FavoriteCategory.ANIME -> AnimeDetails(id)
                                        FavoriteCategory.MANGA -> MangaDetails(id)
                                        FavoriteCategory.CHARACTER -> CharacterDetails(id)
                                        else -> Staff(id)
                                    }

                                    navOptions.navigateToDetails(detailsNavRoute)
                                },
                                onStudioClick = { id, name ->
                                    navOptions.navigateToDetails(Studio(id, name))
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