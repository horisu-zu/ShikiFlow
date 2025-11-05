package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentItem
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.presentation.viewmodel.CommentViewModel
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.SortDirection

@Composable
fun CommentSection(
    topicId: String,
    isRefreshing: Boolean,
    onEntityClick: (Converter.EntityType, String) -> Unit,
    onLinkClick: (String) -> Unit,
    onTopicNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
    commentsCount: Int? = null,
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    val commentsState = commentViewModel.comments.collectAsStateWithLifecycle()

    LaunchedEffect(topicId) {
        commentViewModel.getComments(topicId, sortDirection = SortDirection.ASCENDING, limit = 5)
    }

    LaunchedEffect(isRefreshing) {
        if(isRefreshing) {
            commentViewModel.getComments(
                topicId,
                sortDirection = SortDirection.ASCENDING,
                limit = 5,
                isRefresh = true
            )
        }
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when(val value = commentsState.value) {
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = value.message ?: stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
            }
            is Resource.Success -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = buildString {
                                append(stringResource(id = R.string.details_comments))
                                commentsCount?.let { count ->
                                    append(stringResource(id = R.string.details_comments_count, count))
                                }
                            },
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    IconButton(
                        onClick = { onTopicNavigate(topicId) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Navigate to Topic Comments Screen"
                        )
                    }
                }
                value.data?.forEachIndexed { index, comment ->
                    CommentItem(
                        comment = comment,
                        onEntityClick = onEntityClick,
                        onLinkClick = onLinkClick,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
fun CommentItem(
    comment: CommentItem,
    onEntityClick: (type: Converter.EntityType, id: String) -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        RoundedImage(
            model = comment.user.image.x80,
            modifier = Modifier.size(24.dp),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) {
                        append(comment.user.nickname)
                    }
                    append(" · ")
                    withStyle(style = SpanStyle(
                        fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )) {
                        append(formatInstant(comment.createdAt, includeTime = true))
                    }
                }
            )
            ExpandableText(
                descriptionHtml = comment.htmlBody,
                style = MaterialTheme.typography.bodySmall,
                onEntityClick = { type, id -> onEntityClick(type, id) },
                onLinkClick = onLinkClick,
                collapsedMaxLines = Int.MAX_VALUE
            )
        }
    }
}