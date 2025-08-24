package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.CircleShapeButton
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.more.MoreNavOptions
import com.example.shikiflow.presentation.viewmodel.user.AnimeUserRateViewModel
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userRateViewModel: AnimeUserRateViewModel = hiltViewModel(),
    currentUser: CurrentUserQuery.Data?,
    moreNavOptions: MoreNavOptions
) {
    val context = LocalContext.current

    val userRateData = userRateViewModel.userRateData.collectAsState()

    LaunchedEffect(Unit) {
        currentUser?.currentUser?.id?.let { userId ->
            userRateViewModel.loadUserRates(userId.toLong())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineSmall
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { moreNavOptions.navigateBack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Main"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
            )
        }
    ) { innerPadding ->
        when(val userRate = userRateData.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                        )
                ) {
                    val (currentUserBlock, redirectBlock, listsBlock) = createRefs()

                    CurrentUser(
                        userData = currentUser,
                        context = context,
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .constrainAs(currentUserBlock) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                            }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .constrainAs(redirectBlock) {
                                top.linkTo(currentUserBlock.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    ) {
                        CircleShapeButton(
                            label = "Clubs",
                            icon = IconResource.Drawable(R.drawable.ic_group),
                            onClick = { /**/ },
                            modifier = Modifier.weight(1f)
                        )
                        CircleShapeButton(
                            label = "History",
                            icon = IconResource.Drawable(R.drawable.ic_history),
                            onClick = { moreNavOptions.navigateToHistory() },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    TrackSection(
                        userRateData = userRate.data ?: emptyList(),
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .constrainAs(listsBlock) {
                                top.linkTo(redirectBlock.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            }
                    )
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = {
                            currentUser?.currentUser?.id?.let { userId ->
                                userRateViewModel.loadUserRates(userId.toLong(), true)
                            }
                        }
                    )
                }
            }
        }
    }
}