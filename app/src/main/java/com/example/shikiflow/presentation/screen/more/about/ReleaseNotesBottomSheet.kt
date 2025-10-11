package com.example.shikiflow.presentation.screen.more.about

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.utils.Converter
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseNotesBottomSheet(
    currentVersion: String,
    release: GithubRelease,
    onDismiss: () -> Unit,
    onDownloadReleaseClick: () -> Unit,
    showBottomSheet: Boolean
) {
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                ReleaseNotesHeader(
                    currentVersion = currentVersion,
                    latestVersion = release.tagName,
                    releaseDate = release.publishedAt,
                    latestSize = release.assets.firstOrNull()?.size ?: 0L
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
                Button(
                    onClick = onDownloadReleaseClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_download),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = stringResource(R.string.update_download),
                            style = MaterialTheme.typography.labelMedium
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
    latestSize: Long,
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
            text = stringResource(
                R.string.release_notes_update_size,
                Converter.formatFileSize(latestSize.toDouble())
            ),
            style = MaterialTheme.typography.labelMedium
        )
    }
}