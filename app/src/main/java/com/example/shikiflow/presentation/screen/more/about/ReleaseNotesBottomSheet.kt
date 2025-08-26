package com.example.shikiflow.presentation.screen.more.about

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.utils.Converter
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReleaseNotesBottomSheet(
    currentVersion: String,
    release: GithubRelease,
    onDismiss: () -> Unit,
    onDownloadReleaseClick: () -> Unit,
    showBottomSheet: Boolean,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState()

    if (showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismiss
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                ReleaseNotesHeader(
                    currentVersion = currentVersion,
                    latestVersion = release.tagName,
                    releaseDate = release.publishedAt ?: Instant.DISTANT_PAST,
                    latestSize = release.assets.firstOrNull()?.size ?: 0L
                )
                if(!release.body.isNullOrBlank()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                    ) {
                        Text(
                            text = "What's New",
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_download),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Download",
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
            text = "Current Version: $currentVersion",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "Latest Version: $latestVersion",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "Release Date: ${Converter.formatInstant(releaseDate)}",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = "Size: ${Converter.formatFileSize(latestSize.toDouble())}",
            style = MaterialTheme.typography.labelMedium
        )
    }
}