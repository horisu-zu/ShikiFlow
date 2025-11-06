package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.more.MoreNavOptions
import com.example.shikiflow.presentation.viewmodel.user.UserRateViewModel
import com.example.shikiflow.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    currentUserId: String,
    userData: User?,
    moreNavOptions: MoreNavOptions,
    userRateViewModel: UserRateViewModel = hiltViewModel()
) {
    val userRateData by userRateViewModel.userRateData.collectAsStateWithLifecycle()
    val isRefreshing by userRateViewModel.isRefreshing

    val scrollState = rememberScrollState()

    val isAtTop by remember {
        derivedStateOf {
            scrollState.value <= 0
        }
    }

    LaunchedEffect(Unit) {
        userData?.id?.let { userId ->
            userRateViewModel.loadUserRates(userId)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.profile),
                            style = MaterialTheme.typography.headlineSmall
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
                        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                            else MaterialTheme.colorScheme.surface
                    ),
                )
                if(!isAtTop) HorizontalDivider()
            }
        }
    ) { innerPadding ->
        userData?.let {
            when(userRateData) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
                is Resource.Success -> {
                    val horizontalPadding = 16.dp
                    val containerModifier = Modifier.fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)

                    PullToRefreshBox(
                        modifier = Modifier.fillMaxSize()
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                            ),
                        isRefreshing = isRefreshing,
                        onRefresh = {
                            userRateViewModel.loadUserRates(userData.id, isRefresh = true)
                        }
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
                                .padding(
                                    start = horizontalPadding,
                                    end = horizontalPadding,
                                    bottom = 16.dp
                                ),
                            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                        ) {
                            CurrentUser(
                                userData = userData
                            )
                            userRateData.data?.let { rateExpanded ->
                                if(rateExpanded.userRates.isNotEmpty()) {
                                    TrackSection(
                                        isCurrentUser = currentUserId == userData.id,
                                        userRateData = rateExpanded.userRates,
                                        onCompareClick = {
                                            moreNavOptions.navigateToCompare(userData)
                                        },
                                        modifier = containerModifier
                                    )
                                }
                                if(rateExpanded.userFavorites.isNotEmpty()) {
                                    FavoritesSection(
                                        modifier = containerModifier,
                                        favoritesMap = rateExpanded.userFavorites
                                    )
                                }
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = userRateData.message ?: stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = {
                                userRateViewModel.loadUserRates(userData.id)
                            }
                        )
                    }
                }
            }
        }
    }
}