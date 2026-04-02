package com.example.shikiflow.presentation.screen.main.details.common.comment

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.CommentType
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.thread.Thread
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.comment.CommentViewModel
import com.example.shikiflow.utils.WebIntent

@Composable
fun CommentsScreen(
    threadHeader: Thread?,
    screenMode: CommentsScreenMode,
    id: Int,
    navOptions: MediaNavOptions,
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Scaffold { paddingValues ->
        val contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
            top = paddingValues.calculateTopPadding(),
            bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
        )

        when(screenMode) {
            CommentsScreenMode.TOPIC -> {
                TopicCommentsSection(
                    topicId = id,
                    threadHeader = threadHeader,
                    commentViewModel = commentViewModel,
                    context = context,
                    contentPadding = contentPadding,
                    onEntityClick = { entityType, id ->
                        navOptions.navigateByEntity(entityType, id)
                    },
                    onUserClick = { user ->
                        navOptions.navigateToUserProfile(user)
                    }
                )
            }
            CommentsScreenMode.COMMENT -> {
                CommentThreadSection(
                    commentId = id,
                    commentViewModel = commentViewModel,
                    context = context,
                    contentPadding = contentPadding,
                    onEntityClick = { entityType, id ->
                        navOptions.navigateByEntity(entityType, id)
                    },
                    onUserClick = { user ->
                        navOptions.navigateToUserProfile(user)
                    }
                )
            }
        }
    }
}

@Composable
private fun TopicCommentsSection(
    topicId: Int,
    threadHeader: Thread?,
    commentViewModel: CommentViewModel,
    context: Context,
    contentPadding: PaddingValues,
    onEntityClick: (EntityType, Int) -> Unit,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val commentItems = commentViewModel.comments.collectAsLazyPagingItems()

    LaunchedEffect(topicId) {
        commentViewModel.setTopicId(topicId)
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (commentItems.loadState.refresh) {
            is LoadState.Error -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { commentItems.refresh() }
                        )
                    }
                }
            }
            is LoadState.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
            else -> {
                threadHeader?.let { header ->
                    item {
                        ThreadHeaderItem(
                            threadHeader = header,
                            onEntityClick = onEntityClick,
                            onLinkClick = { url -> WebIntent.openUrlCustomTab(context, url) },
                            onUserClick = onUserClick,
                            modifier = Modifier
                        )
                    }
                }
                items(commentItems.itemCount) { index ->
                    commentItems[index]?.let { comment ->
                        CommentItem(
                            comment = comment,
                            onEntityClick = onEntityClick,
                            onLinkClick = { url -> WebIntent.openUrlCustomTab(context, url) },
                            onUserClick = onUserClick,
                            modifier = Modifier
                        )
                    }
                }
                commentItems.apply {
                    when {
                        loadState.append is LoadState.Error -> {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ErrorItem(
                                        message = stringResource(R.string.common_error),
                                        buttonLabel = stringResource(R.string.common_retry),
                                        onButtonClick = { commentItems.retry() }
                                    )
                                }
                            }
                        }
                        loadState.append is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
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
private fun CommentThreadSection(
    commentId: Int,
    commentViewModel: CommentViewModel,
    context: Context,
    contentPadding: PaddingValues,
    onEntityClick: (EntityType, Int) -> Unit,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val commentsState by commentViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(commentId) {
        commentViewModel.setCommentId(commentId)
    }

    commentsState.repliesMap[commentId]?.let { repliesUiState ->
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(repliesUiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            } else if(repliesUiState.errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = repliesUiState.errorMessage,
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { commentViewModel.onRefresh() }
                        )
                    }
                }
            } else {
                repliesUiState.commentsMap.forEach { (commentType, comments) ->
                    item {
                        CommentsMapSection(
                            title = commentType,
                            comments = comments,
                            onEntityClick = onEntityClick,
                            onLinkClick = { url ->
                                WebIntent.openUrlCustomTab(context, url)
                            },
                            onUserClick = onUserClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentsMapSection(
    title: CommentType,
    comments: List<Comment>,
    onEntityClick: (EntityType, Int) -> Unit,
    onLinkClick: (String) -> Unit,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                color = when(title) {
                    CommentType.OP -> MaterialTheme.colorScheme.background
                        else -> MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                }
            )
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        if(title != CommentType.OP) {
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.onSurface)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(
                    text = title.displayValue,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.surface
                    )
                )
            }
        }
        comments.forEach { comment ->
            CommentItem(
                comment = comment,
                onEntityClick = onEntityClick,
                onLinkClick = onLinkClick,
                onUserClick = onUserClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}