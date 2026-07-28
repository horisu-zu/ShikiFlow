package com.example.shikiflow.presentation.screen.more.profile.activity.reply

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.thread.LikeableType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.activity.ActivityType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.screen.main.details.DetailsNavRoute
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentItemPlaceholder
import com.example.shikiflow.presentation.screen.more.profile.ProfileNavOptions
import com.example.shikiflow.presentation.screen.more.profile.activity.ActivityItem
import com.example.shikiflow.presentation.screen.more.profile.activity.ListActivityItemPlaceholder
import com.example.shikiflow.presentation.viewmodel.user.activity.reply.ActivityRepliesViewModel

@Composable
fun ActivityRepliesScreen(
    activityId: Int,
    activityType: ActivityType,
    navOptions: ProfileNavOptions,
    activityRepliesViewModel: ActivityRepliesViewModel = hiltViewModel()
) {
    val preferredTitleType = LocalTitleTypeController.current
    val uiState by activityRepliesViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(activityId) {
        activityRepliesViewModel.setId(activityId)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /*Editor Sheet State*/ },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Open Activity Reply Editor"
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = paddingValues.calculateTopPadding(),
                bottom = WindowInsets.navigationBars
                    .asPaddingValues()
                    .calculateBottomPadding() + 64.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            if (uiState.errorMessage != null && uiState.replies.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { activityRepliesViewModel.refresh() }
                        )
                    }
                }
            } else if (uiState.isLoading || uiState.isLoadingActivity) {
                item {
                    when (activityType) {
                        ActivityType.LIST -> {
                            ListActivityItemPlaceholder(itemIndex = 0)
                        }
                        else -> {
                            CommentItemPlaceholder(
                                itemIndex = 0,
                                backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        }
                    }
                }

                items(count = 12) { index ->
                    CommentItemPlaceholder(
                        itemIndex = index,
                        backgroundColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                }
            } else {
                uiState.activity?.let { userActivity ->
                    item {
                        ActivityItem(
                            userActivity = userActivity,
                            titleType = preferredTitleType,
                            onListActivityClick = { mediaType, id ->
                                val detailsNavRoute = when(mediaType) {
                                    MediaType.ANIME -> DetailsNavRoute.AnimeDetails(id)
                                    MediaType.MANGA -> DetailsNavRoute.MangaDetails(id)
                                }

                                navOptions.navigateToDetails(detailsNavRoute)
                            },
                            onEntityClick = { entityType, id ->
                                navOptions.navigateByEntity(entityType, id)
                            },
                            onLikeToggle = {
                                activityRepliesViewModel.toggleLike(
                                    id = userActivity.id,
                                    type =  LikeableType.ACTIVITY
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                items(uiState.replies) { activityReply ->
                    ActivityReplyItem(
                        activityReply = activityReply,
                        currentUserId = uiState.currentUserId,
                        onUserClick = { user ->
                            navOptions.navigateToProfile(user)
                        },
                        onEntityClick = { entityType, id ->
                            navOptions.navigateByEntity(entityType, id)
                        },
                        onLikeToggle = { replyId ->
                            activityRepliesViewModel.toggleLike(
                                id = replyId,
                                type = LikeableType.ACTIVITY_REPLY
                            )
                        },
                        onEditClick = { /*Editor Sheet*/ },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (uiState.isLoading) {
                    items(count = 3) { index ->
                        CommentItemPlaceholder(
                            itemIndex = index
                        )
                    }
                } else if (uiState.errorMessage != null) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            ErrorItem(
                                message = stringResource(R.string.common_error),
                                buttonLabel = stringResource(R.string.common_retry),
                                onButtonClick = { activityRepliesViewModel.retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}