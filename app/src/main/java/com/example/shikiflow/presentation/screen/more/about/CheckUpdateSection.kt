package com.example.shikiflow.presentation.screen.more.about

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.utils.Resource

@Composable
fun CheckUpdateSection(
    releaseState: Resource<GithubRelease?>,
    isLatest: Boolean,
    onButtonClick: () -> Unit,
    onDownloadClick: (String) -> Unit,
    onShowBottomSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedContent(
        targetState = isLatest,
        modifier = modifier
    ) { targetState ->
        if(!targetState) {
            releaseState.data?.let { latestRelease ->
                LatestReleaseItem(
                    latestRelease = latestRelease,
                    onDownloadClick = onDownloadClick,
                    showBottomSheet = onShowBottomSheet,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            CheckUpdateButton(
                releaseState = releaseState,
                onButtonClick = onButtonClick
            )
        }
    }
}

@Composable
private fun CheckUpdateButton(
    releaseState: Resource<GithubRelease?>,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        enabled = releaseState !is Resource.Loading,
        contentPadding = PaddingValues(vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.primary
        ),
        onClick = onButtonClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            when(releaseState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = stringResource(R.string.about_checking_updates),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is Resource.Success -> {
                    if(releaseState.data != null) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = stringResource(R.string.about_latest_version),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = stringResource(R.string.about_check_updates),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = stringResource(R.string.about_latest_error),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}