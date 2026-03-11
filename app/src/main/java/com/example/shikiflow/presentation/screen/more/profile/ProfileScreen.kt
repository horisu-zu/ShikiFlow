package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.more.MoreNavOptions
import com.example.shikiflow.presentation.viewmodel.user.UserRateViewModel
import com.example.shikiflow.utils.Resource

@Composable
fun ProfileScreen(
    currentUserId: String?,
    userData: User?,
    moreNavOptions: MoreNavOptions,
    userRateViewModel: UserRateViewModel = hiltViewModel()
) {
    val userRateData by userRateViewModel.userRateStats.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        userData?.id?.let { userId ->
            userRateViewModel.loadUserRates(userId)
        }
    }

    userData?.let {
        when(userRateData) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                ProfileScreenContent(
                    userData = userData,
                    userRateData = userRateData.data,
                    isCurrentUser = currentUserId == userData.id,
                    moreNavOptions = moreNavOptions,
                    modifier = Modifier
                )
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
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