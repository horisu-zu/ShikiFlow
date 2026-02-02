package com.example.shikiflow.presentation.screen.main.details.common

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.comment.ThreadsViewModel
import com.example.shikiflow.utils.Converter.convertInstantToString
import com.example.shikiflow.utils.IconResource
import kotlin.time.Instant

@Composable
fun ThreadsScreen(
    mediaId: Int,
    navOptions: MediaNavOptions,
    threadsViewModel: ThreadsViewModel = hiltViewModel()
) {
    val threadsState = threadsViewModel.paginatedThreads.collectAsLazyPagingItems()
    val resources = LocalResources.current

    LaunchedEffect(mediaId) {
        threadsViewModel.setMediaId(mediaId)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                    end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                ),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = paddingValues.calculateTopPadding(),
                bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            if(threadsState.loadState.refresh is LoadState.Loading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            } else if(threadsState.loadState.append is LoadState.Error) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { threadsState.refresh() }
                        )
                    }
                }
            } else {
                items(threadsState.itemCount) { index ->
                    threadsState[index]?.let { threadData ->
                        ThreadItem(
                            threadData = threadData,
                            resources = resources,
                            onThreadClick = { id ->
                                navOptions.navigateToComments(
                                    screenMode = CommentsScreenMode.TOPIC,
                                    threadHeader = threadData,
                                    id = id
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                threadsState.apply {
                    when {
                        loadState.append is LoadState.Error -> {
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
                                        onButtonClick = { threadsState.refresh() }
                                    )
                                }
                            }
                        }
                        loadState.append is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) { CircularProgressIndicator() }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThreadItem(
    threadData: Thread,
    resources: Resources,
    onThreadClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onThreadClick(threadData.id) }
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            threadData.title?.let { threadTitle ->
                Text(
                    text = threadTitle,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            ThreadStatsItem(
                viewCount = threadData.viewCount,
                replyCount = threadData.replyCount,
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(
                        alpha = 0.8f
                    )
                )
            )
        }
        if(threadData.lastReplyUser != null && threadData.lastRepliedAt != null) {
            ShortUserItem(
                userData = threadData.lastReplyUser,
                replyInstant = threadData.lastRepliedAt,
                resources = resources,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ShortUserItem(
    userData: User,
    replyInstant: Instant,
    resources: Resources,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseImage(
            model = userData.avatarUrl,
            imageType = ImageType.Square(
                defaultWidth = 24.dp,
                defaultClip = RoundedCornerShape(4.dp)
            )
        )
        Text(
            text = buildString {
                append(userData.nickname)
                append(stringResource(R.string.thread_item_user_replied))
                append(convertInstantToString(resources, replyInstant))
            },
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ThreadStatsItem(
    viewCount: Int,
    replyCount: Int,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
    ) {
        TextWithIcon(
            text = viewCount.toString(),
            iconResources = listOf(IconResource.Drawable(resId = R.drawable.ic_eye_filled)),
            style = textStyle
        )
        TextWithIcon(
            text = replyCount.toString(),
            iconResources = listOf(IconResource.Drawable(resId = R.drawable.ic_bubble_filled)),
            style = textStyle
        )
    }
}