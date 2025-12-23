package com.example.shikiflow.presentation.screen.more.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.GithubRelease
import com.example.shikiflow.utils.Converter.formatInstant
import kotlin.time.Instant

@Composable
fun CurrentVersionItem(
    currentVersion: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_shikilogo),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = stringResource(R.string.app_name),
            style = MaterialTheme.typography.titleMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        )
        Text(
            text = "v$currentVersion",
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        )
        Text(
            text = stringResource(
                R.string.version_from,
                formatInstant(Instant.fromEpochMilliseconds(BuildConfig.VERSION_TIMESTAMP), true)
            ),
            style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )
        )
    }
}