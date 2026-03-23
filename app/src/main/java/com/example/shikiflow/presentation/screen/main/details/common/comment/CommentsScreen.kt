package com.example.shikiflow.presentation.screen.main.details.common.comment

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
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
            bottom = 12.dp
        )

        when(screenMode) {
            CommentsScreenMode.TOPIC -> {
                TopicCommentsSection(
                    topicId = id,
                    threadHeader = threadHeader,
                    commentViewModel = commentViewModel,
                    context = context,
                    modifier = Modifier.padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                    ),
                    contentPadding = contentPadding,
                    onEntityClick = { entityType, id ->
                        navOptions.navigateByEntity(entityType, id)
                    }
                )
            }
            CommentsScreenMode.COMMENT -> {
                CommentThreadSection(
                    commentId = id,
                    commentViewModel = commentViewModel,
                    context = context,
                    modifier = Modifier.padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    ),
                    contentPadding = contentPadding,
                    onEntityClick = { entityType, id ->
                        navOptions.navigateByEntity(entityType, id)
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
    modifier: Modifier = Modifier
) {
    val paginatedComments = commentViewModel.paginatedComments(topicId).collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (paginatedComments.loadState.refresh) {
            is LoadState.Error -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = stringResource(R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { paginatedComments.refresh() }
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
                            modifier = Modifier
                        )
                    }
                }
                items(paginatedComments.itemCount) { index ->
                    paginatedComments[index]?.let { comment ->
                        CommentItem(
                            comment = comment,
                            onEntityClick = onEntityClick,
                            onLinkClick = { url -> WebIntent.openUrlCustomTab(context, url) },
                            modifier = Modifier
                        )
                    }
                }
                paginatedComments.apply {
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
                                        onButtonClick = { paginatedComments.retry() }
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
    modifier: Modifier = Modifier
) {
    val commentsState by commentViewModel.repliesUiState.collectAsStateWithLifecycle()

    LaunchedEffect(commentId) {
        commentViewModel.getCommentWithReplies(commentId)
    }

    commentsState[commentId]?.let { repliesUiState ->
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
                            onButtonClick = { commentViewModel.getCommentWithReplies(commentId) }
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
                            }
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
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color = when(title) {
                CommentType.OP -> MaterialTheme.colorScheme.background
                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            }).padding(horizontal = 8.dp, vertical = 12.dp),
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
            )
        }
    }
}