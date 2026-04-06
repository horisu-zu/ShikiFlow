package com.example.shikiflow.presentation.screen.more.about

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import com.example.shikiflow.presentation.viewmodel.more.about.AboutUiState

@Composable
fun CheckUpdateSection(
    uiState: AboutUiState,
    onButtonClick: () -> Unit,
    onDownloadClick: (String) -> Unit,
    onShowBottomSheet: () -> Unit,
    modifier: Modifier = Modifier
) {
    val latestVersion = uiState.latestRelease
    val isLatest = if (latestVersion == null) { true } else {
        "v${uiState.currentRelease.tagName}" == latestVersion.tagName
    }

    AnimatedContent(
        targetState = isLatest,
        transitionSpec = {
            expandVertically(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            ) togetherWith fadeOut(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        },
        modifier = modifier
    ) { targetState ->
        if(!targetState) {
            uiState.latestRelease?.let { latestRelease ->
                LatestReleaseItem(
                    latestRelease = latestRelease,
                    onDownloadClick = onDownloadClick,
                    showBottomSheet = onShowBottomSheet,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            }
        } else {
            CheckUpdateButton(
                uiState = uiState,
                onButtonClick = onButtonClick
            )
        }
    }
}

@Composable
private fun CheckUpdateButton(
    uiState: AboutUiState,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier.fillMaxWidth(),
        enabled = !uiState.isLoading,
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
            if(uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = stringResource(R.string.about_checking_updates),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else if(uiState.errorMessage != null) {
                Text(
                    text = stringResource(R.string.about_latest_error),
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                when(uiState.latestRelease) {
                    null -> {
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
                    else -> {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = stringResource(R.string.about_latest_version),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}