package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun TrackSection(
    isCurrentUser: Boolean,
    userRateData: Map<MediaType, Map<Int, Int>>,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile_screen_track_lists_label),
                style = MaterialTheme.typography.titleLarge
            )

            if(!isCurrentUser) {
                Row(
                    modifier = Modifier.clip(CircleShape)
                        .clickable { onCompareClick() }
                        .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(2.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.more_profile_compare),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        userRateData.entries.forEachIndexed { index, (mediaType, ratesMap) ->
            if (index > 0) {
                HorizontalDivider(modifier = Modifier.ignoreHorizontalParentPadding(12.dp))
            }

            TrackItem(
                iconResource = when (mediaType) {
                    MediaType.ANIME -> IconResource.Drawable(R.drawable.ic_anime)
                    MediaType.MANGA -> IconResource.Drawable(R.drawable.ic_manga)
                },
                type = when (mediaType) {
                    MediaType.ANIME -> stringResource(R.string.main_track_mode_anime)
                    MediaType.MANGA -> stringResource(R.string.main_track_mode_manga)
                },
                groupedData = ratesMap.mapKeys { (resId, _) ->
                    stringResource(resId)
                },
                itemsCount = ratesMap.values.sum()
            )
        }
    }
}