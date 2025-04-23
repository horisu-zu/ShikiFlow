package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.graphql.CurrentUserQuery
import com.example.shikiflow.R
import com.example.shikiflow.presentation.common.CircleShapeButton
import com.example.shikiflow.presentation.screen.more.MoreNavRoute
import com.example.shikiflow.presentation.viewmodel.user.AnimeUserRateViewModel
import com.example.shikiflow.utils.IconResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userRateViewModel: AnimeUserRateViewModel = hiltViewModel(),
    currentUser: CurrentUserQuery.Data?,
    navController: NavController
) {
    val context = LocalContext.current

    val userRateData = userRateViewModel.userRateData.collectAsState()

    LaunchedEffect(Unit) {
        if(userRateData.value.isEmpty()) {
            currentUser?.currentUser?.id?.toLong()
                ?.let { userRateViewModel.loadUserRates(userId = it) }
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
                    IconButton(onClick = { navController.popBackStack() }) {
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
                modifier = Modifier.padding(top = 24.dp)
            )
        }
    ) { paddingValues ->
        if(userRateData.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                        onClick = {
                            navController.navigate(MoreNavRoute.ClubsScreen)
                        },
                        modifier = Modifier.weight(1f)
                    )
                    CircleShapeButton(
                        label = "History",
                        icon = IconResource.Drawable(R.drawable.ic_history),
                        onClick = {
                            navController.navigate(MoreNavRoute.HistoryScreen)
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                TrackSection(
                    userRateData = userRateData.value,
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
    }
}