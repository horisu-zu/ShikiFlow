package com.example.shikiflow.presentation.screen.main.details.common

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.data.common.comment.CommentItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.presentation.viewmodel.CommentViewModel
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.Resource

@Composable
fun CommentSection(
    topicId: String,
    onEntityClick: (Converter.EntityType, String) -> Unit,
    modifier: Modifier = Modifier,
    commentViewModel: CommentViewModel = hiltViewModel()
) {
    val commentsState = commentViewModel.comments.collectAsStateWithLifecycle()

    LaunchedEffect(topicId) {
        commentViewModel.getComments(topicId, limit = 5)
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        when(val value = commentsState.value) {
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${value.message}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Comments",
                        style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(
                        onClick = { /*Navigate to Topic Comments Page*/ }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Add Comment"
                        )
                    }
                }
                value.data?.forEach { comment ->
                    CommentItem(
                        comment = comment,
                        onEntityClick = onEntityClick,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: CommentItem,
    onEntityClick: (type: Converter.EntityType, id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
    ) {
        RoundedImage(
            model = comment.user.image.x80,
            size = 40.dp
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
                onEntityClick = { type, id -> onEntityClick(type, id) }
            )
        }
    }
}