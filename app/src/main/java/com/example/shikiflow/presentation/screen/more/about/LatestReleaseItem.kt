package com.example.shikiflow.presentation.screen.more.about

import android.content.Context
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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.data.common.GithubRelease
import com.example.shikiflow.utils.WebIntent

@Composable
fun LatestReleaseItem(
    latestRelease: GithubRelease,
    context: Context,
    showBottomSheet: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_update),
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )
            Text(
                text = "Update Available",
                style = MaterialTheme.typography.titleLarge
            )
        }
        Text(
            buildAnnotatedString {
                append("Version: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                    append(latestRelease.tagName)
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            TextButton(
                onClick = { showBottomSheet() }
            ) {
                Text(
                    text = "Release Notes",
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            Button(
                onClick = { WebIntent.openUrlCustomTab(
                    context = context,
                    url = latestRelease.assets.first().downloadUrl)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
            ) {
                Text(
                    text = "Download",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}