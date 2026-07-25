package com.example.shikiflow.presentation.screen.main.details.common.comment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.shimmerEffect
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.comment.section.CommentSectionViewModel
import com.example.shikiflow.utils.IconResource

@Composable
fun CommentSection(
    topicId: Int,
    navOptions: MediaNavOptions,
    modifier: Modifier = Modifier,
    commentSectionViewModel: CommentSectionViewModel = hiltViewModel(key = topicId.toString())
) {
    val uiState by commentSectionViewModel.uiState.collectAsStateWithLifecycle()
    val editorSheetState = remember { mutableStateOf<EditorSheetState?>(null) }

    LaunchedEffect(topicId) {
        commentSectionViewModel.setTopicId(topicId)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextWithDivider(
                text = stringResource(id = R.string.details_comments),
                modifier = Modifier.weight(1f)
            )

            IconButton(
                shape = RoundedCornerShape(percent = 24),
                onClick = { commentSectionViewModel.onRefresh() },
                enabled = !uiState.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh the comments"
                )
            }

            IconButton(
                shape = RoundedCornerShape(percent = 24),
                onClick = { navOptions.navigateToComments(CommentsScreenMode.TOPIC, topicId) }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Navigate to Topic Comments Screen"
                )
            }
        }

        if (uiState.isLoading) {
            repeat(5) { index ->
                CommentItemPlaceholder(
                    backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
                    itemIndex = index
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(all = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp + 24.dp)
                    .shimmerEffect()
            )
        } else if(uiState.errorMessage != null) {
            Box(
                modifier = Modifier.height(240.dp),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = uiState.errorMessage ?: stringResource(R.string.common_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { commentSectionViewModel.onRefresh() },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            uiState.comments.takeLast(5).forEach { comment ->
                CommentItem(
                    comment = comment,
                    currentUserId = uiState.currentUserId ?: 0,
                    onEntityClick = { entityType, id ->
                        navOptions.navigateByEntity(entityType, id)
                    },
                    onUserClick = { user ->
                        navOptions.navigateToUserProfile(user)
                    },
                    onLikeToggle = { /**/ },
                    onCommentSelect = { /**/ },
                    onReplyClick = { commentId ->
                        editorSheetState.value = EditorSheetState(
                            threadId = topicId,
                            parentCommentId = commentId
                        )
                    },
                    onEditClick = { commentId, markdownBody ->
                        editorSheetState.value = EditorSheetState(
                            threadId = topicId,
                            commentId = commentId,
                            commentBody = markdownBody
                        )
                    }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(all = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        editorSheetState.value = EditorSheetState(
                            threadId = topicId
                        )
                    }
                    .background(MaterialTheme.colorScheme.background)
                    .padding(all = 12.dp),
                contentAlignment = Alignment.TopStart
            ) {
                TextWithIcon(
                    text = stringResource(R.string.comment_editor_text_placeholder),
                    iconResources = listOf(IconResource.Vector(imageVector = Icons.Default.Edit)),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    editorSheetState.value?.let { editorState ->
        CommentEditorSheet(
            threadId = editorState.threadId,
            commentId = editorState.commentId,
            commentBody = editorState.commentBody,
            parentCommentId = editorState.parentCommentId,
            onDismiss = { editorSheetState.value = null },
            onEvent = { event ->
                commentSectionViewModel.onCommentEvent(event)
                editorSheetState.value = null
            }
        )
    }
}