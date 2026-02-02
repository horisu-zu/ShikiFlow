package com.example.shikiflow.presentation.screen.main.details.common.comment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.example.shikiflow.domain.model.comment.EntityType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.presentation.viewmodel.comment.CommentSectionViewModel

@Composable
fun CommentSection(
    topicId: Int,
    onEntityClick: (EntityType, Int) -> Unit,
    onLinkClick: (String) -> Unit,
    onTopicNavigate: (Int) -> Unit,
    modifier: Modifier = Modifier,
    commentsCount: Int? = null,
    commentSectionViewModel: CommentSectionViewModel = hiltViewModel(key = topicId.toString())
) {
    val previewCommentsState by commentSectionViewModel.previewComments.collectAsStateWithLifecycle()

    LaunchedEffect(topicId) {
        commentSectionViewModel.setTopicId(topicId)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
    ) {
        when(previewCommentsState) {
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = previewCommentsState.message ?: stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            is Resource.Loading -> {
                Box(contentAlignment = Alignment.Center) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
            is Resource.Success -> {
                if(!previewCommentsState.data.isNullOrEmpty()) {
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
                    previewCommentsState.data?.forEach { comment ->
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
}