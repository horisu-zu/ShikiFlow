package com.example.shikiflow.presentation.screen.main.details.common

import android.content.Context
import androidx.browser.customtabs.CustomTabsIntent
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.domain.model.comment.CommentItem
import com.example.shikiflow.domain.model.comment.CommentType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.CommentViewModel
import com.example.shikiflow.utils.Converter.EntityType
import com.example.shikiflow.utils.Resource

enum class CommentsScreenMode {
    TOPIC, COMMENT
}

@Composable
fun CommentsScreen(
    screenMode: CommentsScreenMode,
    id: String,
    navOptions: MediaNavOptions,
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    val customTabIntent = CustomTabsIntent.Builder().build()
    val context = LocalContext.current

    Scaffold { paddingValues ->
        when(screenMode) {
            CommentsScreenMode.TOPIC -> {
                TopicCommentsSection(
                    topicId = id,
                    navOptions = navOptions,
                    customTabIntent = customTabIntent,
                    context = context,
                    commentViewModel = commentViewModel,
                    modifier = Modifier.padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    )
                )
            }
            CommentsScreenMode.COMMENT -> {
                CommentThreadSection(
                    commentId = id,
                    navOptions = navOptions,
                    customTabIntent = customTabIntent,
                    context = context,
                    commentViewModel = commentViewModel,
                    modifier = Modifier.padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    )
                )
            }
        }
    }
}

@Composable
private fun TopicCommentsSection(
    topicId: String,
    navOptions: MediaNavOptions,
    customTabIntent: CustomTabsIntent,
    context: Context,
    commentViewModel: CommentViewModel,
    modifier: Modifier = Modifier
) {
    val paginatedComments = commentViewModel.paginatedComments(topicId).collectAsLazyPagingItems()

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(paginatedComments.itemCount) { index ->
            paginatedComments[index]?.let { comment ->
                CommentItem(
                    comment = comment,
                    onEntityClick = { entityType, id ->
                        when(entityType) {
                            EntityType.CHARACTER -> {
                                navOptions.navigateToCharacterDetails(id)
                            }
                            EntityType.PERSON -> {
                                customTabIntent.launchUrl(context,
                                    "${BuildConfig.BASE_URL}/person/$id".toUri()
                                )
                            }
                            EntityType.ANIME -> {
                                navOptions.navigateToAnimeDetails(id)
                            }
                            EntityType.MANGA -> {
                                navOptions.navigateToMangaDetails(id)
                            }
                            EntityType.COMMENT -> {
                                navOptions.navigateToComments(CommentsScreenMode.COMMENT, id)
                            }
                        }
                    },
                    onLinkClick = { url -> customTabIntent.launchUrl(context, url.toUri()) },
                    modifier = Modifier
                )
            }
        }
        paginatedComments.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator() }
                    }
                }
            }
        }
    }
}

@Composable
private fun CommentThreadSection(
    commentId: String,
    navOptions: MediaNavOptions,
    customTabIntent: CustomTabsIntent,
    context: Context,
    commentViewModel: CommentViewModel,
    modifier: Modifier = Modifier
) {
    val commentsState by commentViewModel.commentsWithReplies.collectAsStateWithLifecycle()

    LaunchedEffect(commentId) {
        commentViewModel.getCommentWithReplies(commentId)
    }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when(commentsState) {
            is Resource.Loading -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            }
            is Resource.Success -> {
                commentsState.data?.let { commentsMap ->
                    commentsMap.forEach { commentType, comments ->
                        item {
                            CommentsMapSection(
                                title = commentType,
                                comments = comments,
                                onEntityClick = { entityType, id ->
                                    when(entityType) {
                                        EntityType.CHARACTER -> {
                                            navOptions.navigateToCharacterDetails(id)
                                        }
                                        EntityType.PERSON -> {
                                            customTabIntent.launchUrl(context,
                                                "${BuildConfig.BASE_URL}/person/$id".toUri()
                                            )
                                        }
                                        EntityType.ANIME -> {
                                            navOptions.navigateToAnimeDetails(id)
                                        }
                                        EntityType.MANGA -> {
                                            navOptions.navigateToMangaDetails(id)
                                        }
                                        EntityType.COMMENT -> {
                                            if(id != commentId) {
                                                navOptions.navigateToComments(CommentsScreenMode.COMMENT, id)
                                            }
                                        }
                                    }
                                }, onLinkClick = { url ->
                                    customTabIntent.launchUrl(context, url.toUri())
                                }
                            )
                        }
                    }
                }
            }
            is Resource.Error -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = "Error: ${commentsState.message}",
                            buttonLabel = "Retry",
                            onButtonClick = { commentViewModel.getCommentWithReplies(commentId) }
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
    comments: List<CommentItem>,
    onEntityClick: (EntityType, String) -> Unit,
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