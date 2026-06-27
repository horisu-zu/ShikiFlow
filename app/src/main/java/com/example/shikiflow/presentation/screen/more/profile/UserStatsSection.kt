package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.screen.more.profile.stats.anilist.AnilistUserStatsSection
import com.example.shikiflow.presentation.screen.more.profile.stats.shikimori.ShikimoriTrackSection
import com.example.shikiflow.presentation.viewmodel.user.statistics.UserStatsViewModel

@Composable
fun UserStatsSection(
    userData: User,
    typesList: List<MediaType>,
    isCurrentUser: Boolean,
    horizontalPadding: Dp,
    navOptions: ProfileNavOptions,
    userStatsViewModel: UserStatsViewModel = hiltViewModel()
) {
    val uiState by userStatsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        userStatsViewModel.setInitialParams(userData.id, typesList)
    }

    uiState.authType?.let { authType ->
        when(authType) {
            AuthType.SHIKIMORI -> {
                ShikimoriTrackSection(
                    userRateData = uiState.overviewStats,
                    typesList = uiState.typesList,
                    currentType = uiState.mediaType,
                    isLoading = uiState.isLoading,
                    errorMessage = uiState.errorMessage,
                    isCurrentUser = isCurrentUser,
                    onTypeSelect = { mediaType ->
                        userStatsViewModel.setMediaType(mediaType)
                    },
                    onRetryClick = { userStatsViewModel.onRefresh(uiState.statsSectionType) },
                    onCompareClick = {
                        navOptions.navigateToCompare(userData)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = horizontalPadding)
                )
            }
            AuthType.ANILIST -> {
                AnilistUserStatsSection(
                    uiState = uiState,
                    isCurrentUser = isCurrentUser,
                    onCompareClick = {
                        navOptions.navigateToCompare(userData)
                    },
                    horizontalPadding = horizontalPadding,
                    event = userStatsViewModel,
                    navOptions = navOptions,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}