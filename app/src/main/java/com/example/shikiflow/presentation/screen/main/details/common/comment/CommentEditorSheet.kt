package com.example.shikiflow.presentation.screen.main.details.common.comment

import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.insert
import androidx.compose.foundation.text.input.maxLength
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.comment.AniListFormat
import com.example.shikiflow.domain.model.comment.MarkdownFormat
import com.example.shikiflow.domain.model.comment.ShikimoriFormat
import com.example.shikiflow.presentation.common.Button
import com.example.shikiflow.presentation.common.CustomDialog
import com.example.shikiflow.presentation.common.CustomTextField
import com.example.shikiflow.presentation.common.ItemWithPopup
import com.example.shikiflow.presentation.common.ProgressBar
import com.example.shikiflow.presentation.common.RichTextRenderer
import com.example.shikiflow.presentation.common.mappers.MarkdownFormatMapper
import com.example.shikiflow.presentation.common.mappers.MarkdownFormatMapper.iconResource
import com.example.shikiflow.presentation.viewmodel.comment.editor.CommentEditorUiState
import com.example.shikiflow.presentation.viewmodel.comment.editor.CommentEditorViewModel
import com.example.shikiflow.presentation.viewmodel.comment.editor.CommentEvent
import com.example.shikiflow.presentation.viewmodel.comment.editor.UploadMediaState
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentEditorSheet(
    threadId: Int,
    commentId: Int?,
    commentBody: String?,
    parentCommentId: Int?,
    onDismiss: () -> Unit,
    onEvent: (CommentEvent) -> Unit,
    commentEditorViewModel: CommentEditorViewModel = hiltViewModel()
) {
    val uiState by commentEditorViewModel.uiState.collectAsStateWithLifecycle()
    val textFieldState = rememberTextFieldState(
        initialText = commentBody ?: ""
    )
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showPreview = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        commentEditorViewModel.commentEvent.collect { event ->
            onEvent(event)
        }
    }

    LaunchedEffect(uiState.authType) {
        if (uiState.authType == AuthType.SHIKIMORI && parentCommentId != null) {
            textFieldState.edit {
                val prefix = "[comment=$parentCommentId], "

                if (!textFieldState.text.startsWith(prefix)) {
                    insert(0, prefix)
                }
            }
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) commentEditorViewModel.setUri(uri)
    }

    if (showDeleteDialog.value) {
        CustomDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            text = stringResource(R.string.comment_editor_delete_label),
            confirmButtonText = stringResource(R.string.common_confirm),
            onConfirm = { commentEditorViewModel.deleteComment(commentId!!) }
        )
    }

    ModalBottomSheet(
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = 12.dp,
                    start = 12.dp,
                    end = 12.dp
                )
                .imePadding()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    enabled = textFieldState.text.isNotBlank(),
                    shape = RoundedCornerShape(percent = 24),
                    onClick = { showPreview.value = !showPreview.value }
                ) {
                    Text(
                        text = when (showPreview.value) {
                            true -> stringResource(R.string.comment_editor_edit_label)
                            false -> stringResource(R.string.comment_editor_preview_label)
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (uiState.authType != null && textFieldState.text.isNotEmpty()) {
                    Text(
                        text = buildString {
                            append(textFieldState.text.length)
                            append("/")
                            append(
                                when (uiState.authType!!) {
                                    AuthType.SHIKIMORI -> 4096
                                    AuthType.ANILIST -> 12000
                                }
                            )
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (commentId != null) {
                    IconButton(
                        shape = RoundedCornerShape(percent = 24),
                        onClick = { showDeleteDialog.value = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            tint = MaterialTheme.colorScheme.error,
                            contentDescription = "Delete Comment",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                AnimatedVisibility(
                    visible = uiState.authType == AuthType.SHIKIMORI
                ) {
                    FilterChip(
                        selected = uiState.isOfftopic,
                        enabled = commentId == null,
                        label = {
                            Text(
                                text = stringResource(R.string.comment_offtopic),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        onClick = { commentEditorViewModel.toggleOfftopic() }
                    )
                }

                Button(
                    label = when (commentId) {
                        null -> stringResource(R.string.comment_editor_publish)
                        else -> stringResource(R.string.comment_editor_update)
                    },
                    shape = RoundedCornerShape(percent = 24),
                    onClick = {
                        commentEditorViewModel.publishComment(
                            commentId = commentId,
                            topicId = threadId,
                            parentCommentId = parentCommentId,
                            commentBody = textFieldState.text.toString(),
                            isOfftopic = uiState.isOfftopic
                        )
                    }
                )
            }

            AnimatedContent(
                targetState = showPreview.value,
                transitionSpec = {
                    if (showPreview.value) {
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
                    }
                }
            ) { showPreview ->
                if (showPreview) {
                    RichTextRenderer(
                        htmlText = textFieldState.text.toString(),
                        onEntityClick = { _, _ -> /**/ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .verticalScroll(rememberScrollState())
                            .clip(RoundedCornerShape(size = 16.dp))
                            .background(MaterialTheme.colorScheme.background)
                            .padding(all = 12.dp)
                    )
                } else {
                    CommentInputComponent(
                        textFieldState = textFieldState,
                        uiState = uiState,
                        launcher = launcher,
                        onSetFormat = { format ->
                            commentEditorViewModel.setFormat(format)
                        },
                        onResetUploadState = { commentEditorViewModel.resetUploadState() },
                        onRetryUpload = { commentEditorViewModel.retryUpload() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentInputComponent(
    textFieldState: TextFieldState,
    uiState: CommentEditorUiState,
    launcher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    onSetFormat: (MarkdownFormat) -> Unit,
    onResetUploadState: () -> Unit,
    onRetryUpload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        CustomTextField(
            textFieldState = textFieldState,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            placeholder = {
                if (textFieldState.text.isEmpty()) {
                    Text(
                        text = stringResource(R.string.comment_editor_text_placeholder),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    )
                }
            },
            lineLimits = TextFieldLineLimits.MultiLine(
                minHeightInLines = 6,
                maxHeightInLines = 12
            ),
            inputTransformation = InputTransformation.maxLength(
                maxLength = when (uiState.authType) {
                    AuthType.SHIKIMORI -> 4096
                    AuthType.ANILIST -> 12000
                    null -> Int.MAX_VALUE
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .verticalScroll(rememberScrollState())
                .clip(RoundedCornerShape(size = 16.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(all = 12.dp)
        )

        AnimatedVisibility(
            visible = uiState.authType != null
        ) {
            EditorMarkdownComponent(
                authType = uiState.authType!!,
                formatSelected = uiState.format != null,
                onFormatClick = { markdownFormat ->
                    when (markdownFormat) {
                        AniListFormat.IMAGE, ShikimoriFormat.IMAGE -> {
                            onSetFormat(markdownFormat)
                            launcher.launch(
                                input = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                        AniListFormat.VIDEO -> {
                            onSetFormat(markdownFormat)
                            launcher.launch(
                                input = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                        else -> {
                            textFieldState.edit {
                                val position = selection.end
                                insert(index = position, markdownFormat.syntax)

                                selection = when (markdownFormat) {
                                    AniListFormat.QUOTE -> {
                                        TextRange(position + markdownFormat.syntax.length)
                                    }
                                    AniListFormat.YOUTUBE -> {
                                        TextRange(position + 8)
                                    }
                                    ShikimoriFormat.LIST -> {
                                        TextRange(position + 9)
                                    }
                                    else -> {
                                        TextRange(position + markdownFormat.syntax.length / 2)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }

        AnimatedVisibility(
            visible = uiState.uploadMediaState !is UploadMediaState.Idle
        ) {
            UploadMediaComponent(
                state = uiState.uploadMediaState,
                onSuccess = { value ->
                    textFieldState.edit {
                        val position = selection.end
                        insert(index = position, value)

                        selection = TextRange(position + value.length)
                    }

                    onResetUploadState()
                },
                onRetry = onRetryUpload,
                onCancel = onResetUploadState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun EditorMarkdownComponent(
    authType: AuthType,
    formatSelected: Boolean,
    onFormatClick: (MarkdownFormat) -> Unit,

) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ItemWithPopup(
            anchor = { onClick ->
                IconButton(
                    shape = RoundedCornerShape(percent = 24),
                    onClick = onClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_format_letter),
                        contentDescription = "Format",
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            popupContent = {
                val rowModifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 6.dp, vertical = 4.dp)

                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = rowModifier,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            MarkdownFormatMapper.listEntries(authType).forEach { markdownFormat ->
                                IconButton(
                                    enabled = !formatSelected,
                                    shape = RoundedCornerShape(percent = 24),
                                    modifier = Modifier.size(32.dp),
                                    onClick = { onFormatClick(markdownFormat) }
                                ) {
                                    markdownFormat.iconResource().toIcon(
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = rowModifier,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            MarkdownFormatMapper.formatEntries(authType).forEach { markdownFormat ->
                                IconButton(
                                    enabled = !formatSelected,
                                    shape = RoundedCornerShape(percent = 24),
                                    modifier = Modifier.size(32.dp),
                                    onClick = { onFormatClick(markdownFormat) }
                                ) {
                                    markdownFormat.iconResource().toIcon(
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        )

        MarkdownFormatMapper.remainingEntries(authType).forEach { markdownFormat ->
            IconButton(
                enabled = !formatSelected,
                shape = RoundedCornerShape(percent = 24),
                modifier = Modifier.size(32.dp),
                onClick = { onFormatClick(markdownFormat) }
            ) {
                markdownFormat.iconResource().toIcon(
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun UploadMediaComponent(
    state: UploadMediaState,
    onSuccess: (String) -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(state) {
        if (state is UploadMediaState.Success) {
            val markdown = when (val format = state.format) {
                is AniListFormat -> format.syntax.replace("%s", state.media.url)
                is ShikimoriFormat -> format.syntax.replace("%s", state.media.bbCode ?: "")
            }

            onSuccess(markdown)
        }
    }

    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn() + slideInVertically() togetherWith fadeOut()
        },
        contentKey = { state ->
            when(state) {
                is UploadMediaState.Idle -> 0
                is UploadMediaState.Uploading -> 1
                is UploadMediaState.Success -> 2
                is UploadMediaState.Error -> 3
            }
        }
    ) { state ->
        when (state) {
            is UploadMediaState.Uploading -> {
                Column(
                    modifier = modifier,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.comment_editor_media_upload_label),
                            style = MaterialTheme.typography.labelLarge
                        )

                        Text(
                            text = buildString {
                                append((state.progress * 100).toInt())
                                append("%")
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier
                                .width(56.dp)
                                .clip(RoundedCornerShape(percent = 32))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(vertical = 6.dp)
                        )
                    }

                    ProgressBar(
                        progress = state.progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            is UploadMediaState.Error -> {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                ) {
                    Text(
                        text = state.message,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextButton(
                            onClick = onCancel
                        ) {
                            Text(
                                text = "Cancel",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Button(
                            label = stringResource(R.string.common_retry),
                            onClick = onRetry
                        )
                    }
                }
            }
            else -> { /**/ }
        }
    }
}