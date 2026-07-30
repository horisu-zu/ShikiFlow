package com.example.shikiflow.presentation.screen.main.details.common.comment

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.ALComment
import com.example.shikiflow.domain.model.comment.ALComment.Companion.findComment
import com.example.shikiflow.domain.model.comment.Comment
import com.example.shikiflow.domain.model.comment.CommentType
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.player.LocalExoPlayerCache
import com.example.shikiflow.presentation.common.player.rememberExoPlayerCache
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.comment.CommentViewModel
import com.example.shikiflow.presentation.viewmodel.comment.reply.CommentRepliesViewModel
import com.example.shikiflow.utils.LazyListUtils.onBottomReached
import kotlinx.coroutines.FlowPreview

@Composable
fun CommentsScreen(
    screenMode: CommentsScreenMode,
    id: Int,
    navOptions: MediaNavOptions
) {
    val exoPlayerCache = rememberExoPlayerCache()

    CompositionLocalProvider(
        LocalExoPlayerCache provides exoPlayerCache
    ) {
        Scaffold { paddingValues ->
            val contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = paddingValues.calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 64.dp
            )

            when(screenMode) {
                CommentsScreenMode.TOPIC -> {
                    TopicCommentsSection(
                        topicId = id,
                        contentPadding = contentPadding,
                        onEntityClick = { entityType, id ->
                            navOptions.navigateByEntity(entityType, id)
                        },
                        onUserClick = { user ->
                            navOptions.navigateToUserProfile(user)
                        }
                    )
                }
                CommentsScreenMode.REPLY -> {
                    CommentWithRepliesSection(
                        commentId = id,
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
}

@OptIn(FlowPreview::class)
@Composable
private fun TopicCommentsSection(
    topicId: Int,
    contentPadding: PaddingValues,
    onEntityClick: (EntityType, Int) -> Unit,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    val uiState by commentViewModel.uiState.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val editorSheetState = remember { mutableStateOf<EditorSheetState?>(null) }

    val onReplyClick: (Int) -> Unit = { commentId ->
        editorSheetState.value = EditorSheetState(
            id = topicId,
            parentCommentId = commentId
        )
    }

    val onEditClick: (Int, String) -> Unit = { commentId, markdownBody ->
        editorSheetState.value = EditorSheetState(
            id = topicId,
            entryId = commentId,
            body = markdownBody
        )
    }

    if (!uiState.isLoading) {
        lazyListState.onBottomReached(
            buffer = 5,
            onLoadMore = { commentViewModel.onLoadMore() }
        )
    }

    LaunchedEffect(topicId) {
        commentViewModel.setTopicId(topicId)
    }

    BackHandler(enabled = uiState.navState.isNotEmpty()) {
        commentViewModel.removeCommentFromStack()
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        AnimatedContent(
            targetState = uiState.navState,
            transitionSpec = {
                if (targetState.size > initialState.size) {
                    slideInHorizontally(
                        initialOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                } else {
                    slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { it },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    )
                } using SizeTransform(clip = false)
            }
        ) { navState ->
            val targetId = remember(navState) {
                navState.lastOrNull()
            }

            if (targetId != null) {
                val comment = uiState.comments.firstNotNullOfOrNull { root ->
                    (root as ALComment).findComment(targetId)
                }

                comment?.let {
                    CommentItem(
                        comment = comment,
                        currentUserId = uiState.currentUserId ?: 0,
                        onEntityClick = onEntityClick,
                        onUserClick = onUserClick,
                        onLikeToggle = { commentId ->
                            commentViewModel.toggleCommentLike(commentId)
                        },
                        onCommentSelect = { commentId ->
                            commentViewModel.selectComment(commentId)
                        },
                        onReplyClick = onReplyClick,
                        onEditClick = onEditClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(contentPadding)
                    )
                }
            } else {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = contentPadding,
                    userScrollEnabled = !(uiState.isLoading && uiState.comments.isEmpty()),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                ) {
                    val showInitPlaceholders = when (uiState.authType) {
                        null -> true
                        AuthType.ANILIST -> uiState.isLoadingThread
                        else -> uiState.comments.isEmpty()
                    }

                    if (uiState.errorMessage != null && uiState.comments.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillParentMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                ErrorItem(
                                    message = stringResource(R.string.common_error),
                                    buttonLabel = stringResource(R.string.common_retry),
                                    onButtonClick = { commentViewModel.refresh() }
                                )
                            }
                        }
                    } else if (uiState.isLoading && showInitPlaceholders) {
                        item {
                            ThreadHeaderItemPlaceholder()
                        }

                        items(count = 12) { index ->
                            CommentItemPlaceholder(
                                backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                                itemIndex = index,
                                maxValue = 4
                            )
                        }
                    } else {
                        uiState.thread?.let { threadHeader ->
                            item {
                                ThreadHeaderItem(
                                    threadHeader = threadHeader,
                                    onEntityClick = onEntityClick,
                                    onUserClick = onUserClick,
                                    onLikeToggle = { threadId ->
                                        commentViewModel.toggleThreadLike(threadId)
                                    }
                                )
                            }
                        }

                        items(
                            items = uiState.comments,
                            key = { comment -> comment.id }
                        ) { comment ->
                            CommentItem(
                                comment = comment,
                                currentUserId = uiState.currentUserId ?: 0,
                                onEntityClick = onEntityClick,
                                onUserClick = onUserClick,
                                onLikeToggle = { commentId ->
                                    commentViewModel.toggleCommentLike(commentId)
                                },
                                onCommentSelect = { commentId ->
                                    commentViewModel.selectComment(commentId)
                                },
                                onReplyClick = onReplyClick,
                                onEditClick = onEditClick
                            )
                        }

                        if (uiState.isLoading) {
                            items(3) { index ->
                                CommentItemPlaceholder(
                                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
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
                                        onButtonClick = { commentViewModel.retry() }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { editorSheetState.value = EditorSheetState(id = topicId) },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Navigate to the Comment Editor"
            )
        }
    }

    editorSheetState.value?.let { editorState ->
        CommentEditorSheet(
            commentId = editorState.entryId,
            commentBody = editorState.body,
            parentCommentId = editorState.parentCommentId,
            onDismiss = { editorSheetState.value = null },
            onSubmit = { commentBody, isOfftopic ->
                commentViewModel.submitComment(
                    commentId = editorState.entryId,
                    topicId = editorState.id,
                    parentCommentId = editorState.parentCommentId,
                    commentBody = commentBody,
                    isOfftopic = isOfftopic
                )
            },
            onDelete = { commentId ->
                commentViewModel.deleteComment(commentId)
            }
        )
    }
}

@Composable
private fun CommentWithRepliesSection(
    commentId: Int,
    contentPadding: PaddingValues,
    onEntityClick: (EntityType, Int) -> Unit,
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier,
    commentRepliesViewModel: CommentRepliesViewModel = hiltViewModel()
) {
    val commentsState by commentRepliesViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(commentId) {
        commentRepliesViewModel.setCommentId(commentId)
    }

    commentsState.repliesMap[commentId]?.let { repliesUiState ->
        LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if(repliesUiState.isLoading) {
                items(count = 3) { index ->
                    CommentItemPlaceholder(
                        itemIndex = index
                    )
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
                            onButtonClick = { commentRepliesViewModel.onRefresh() }
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
    onUserClick: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
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
                currentUserId = 0,
                onEntityClick = onEntityClick,
                onUserClick = onUserClick,
                onLikeToggle = { /**/ }, //Shouldn't happen as it's Shikimori API only section
                onCommentSelect = { /**/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
        }
    }
}