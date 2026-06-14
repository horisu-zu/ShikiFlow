package com.example.shikiflow.presentation.screen.more.about

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.FileSize
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.presentation.common.Button
import com.example.shikiflow.presentation.common.CheckboxItem
import com.example.shikiflow.presentation.common.ProgressBar
import com.example.shikiflow.presentation.viewmodel.more.about.AboutEvent
import com.example.shikiflow.presentation.viewmodel.more.about.UpdateState
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.Converter.formatFileSize
import com.example.shikiflow.utils.IconResource
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseNotesBottomSheet(
    currentVersion: String,
    release: GithubRelease,
    updateState: UpdateState,
    autoInstall: Boolean,
    onDismiss: () -> Unit,
    event: AboutEvent
) {
    val onDownload = {
        val asset = release.assets.firstOrNull { asset ->
            asset.releaseName.contains(Build.SUPPORTED_ABIS.first())
        } ?: release.assets.first()

        event.downloadRelease(asset.releaseName, asset.downloadUrl)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            ReleaseNotesHeader(
                currentVersion = currentVersion,
                latestVersion = release.tagName,
                releaseDate = release.publishedAt,
                latestSize = release.assets.firstOrNull()?.let { asset ->
                    formatFileSize(asset.size.toDouble())
                } ?: FileSize(value = 0.0, unit = FileSize.SizeUnit.B)
            )

            if(!release.body.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                ) {
                    Text(
                        text = stringResource(R.string.release_notes_whats_new),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = release.body,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            AnimatedContent(
                targetState = updateState,
                transitionSpec = {
                    fadeIn() + slideInVertically() togetherWith fadeOut()
                },
                contentKey = { state ->
                    when(state) {
                        UpdateState.Idle -> 0
                        is UpdateState.Updating -> 1
                        is UpdateState.Completed -> 2
                        is UpdateState.Error -> 3
                    }
                }
            ) { updateState ->
                when(updateState) {
                    UpdateState.Idle -> {
                        Button(
                            icon = IconResource.Drawable(R.drawable.ic_download),
                            label = stringResource(R.string.update_download),
                            shape = RoundedCornerShape(percent = 24),
                            onClick = onDownload,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is UpdateState.Updating -> {
                        UpdateProgressBar(
                            updateState = updateState,
                            autoInstall = autoInstall,
                            onAutoInstallChange = { newValue ->
                                event.setAutoInstall(newValue)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is UpdateState.Completed -> {
                        Button(
                            icon = IconResource.Drawable(R.drawable.ic_download),
                            label = stringResource(R.string.release_notes_install_update),
                            shape = RoundedCornerShape(percent = 24),
                            onClick = {
                                event.installRelease(updateState.fileName)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    is UpdateState.Error -> {
                        UpdateErrorItem(
                            errorMessage = updateState.message,
                            onRetryClick = onDownload,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReleaseNotesHeader(
    currentVersion: String,
    latestVersion: String,
    releaseDate: Instant,
    latestSize: FileSize,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Text(
            text = stringResource(R.string.release_notes_current_version, currentVersion),
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = stringResource(R.string.release_notes_latest_version, latestVersion),
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = stringResource(
                R.string.release_notes_release_date,
                Converter.formatInstant(releaseDate)
            ),
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = buildString {
                append(stringResource(R.string.release_notes_update_size))
                append(": ")
                append(
                    when(latestSize.unit) {
                        FileSize.SizeUnit.B -> stringResource(R.string.cache_size_bytes, latestSize.value.toLong())
                        FileSize.SizeUnit.KB -> stringResource(R.string.cache_size_kbytes, latestSize.value)
                        FileSize.SizeUnit.MB -> stringResource(R.string.cache_size_mbytes, latestSize.value)
                        FileSize.SizeUnit.GB -> stringResource(R.string.cache_size_gbytes, latestSize.value)
                    }
                )
            },
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun UpdateProgressBar(
    updateState: UpdateState.Updating,
    autoInstall: Boolean,
    onAutoInstallChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(percent = 16))
            .background(MaterialTheme.colorScheme.surface)
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.release_notes_updating),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = buildString {
                    append((updateState.progress * 100).toInt())
                    append("%")
                },
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(percent = 32))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .animateContentSize()
            )
        }

        ProgressBar(
            progress = updateState.progress,
            modifier = Modifier.fillMaxWidth()
        )

        CheckboxItem(
            label = stringResource(R.string.release_notes_auto_install_label),
            isSelected = autoInstall,
            onToggle = { isSelected ->
                onAutoInstallChange(!isSelected)
            }
        )
    }
}

@Composable
private fun UpdateErrorItem(
    errorMessage: String,
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.labelMedium
        )

        TextButton(
            onClick = onRetryClick,
            shape = RoundedCornerShape(percent = 32)
        ) {
            Text(
                text = stringResource(R.string.common_retry)
            )
        }
    }
}