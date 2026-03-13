package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
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
import com.example.shikiflow.domain.model.user.MediaTypeStats
import com.example.shikiflow.utils.IconResource

@Composable
fun TrackSection(
    isCurrentUser: Boolean,
    userRateData: Map<MediaType, MediaTypeStats>,
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

        userRateData.entries.forEach { (mediaType, ratesList) ->
            TrackItem(
                mediaType = mediaType,
                iconResource = when (mediaType) {
                    MediaType.ANIME -> IconResource.Drawable(R.drawable.ic_anime)
                    MediaType.MANGA -> IconResource.Drawable(R.drawable.ic_manga)
                },
                type = when (mediaType) {
                    MediaType.ANIME -> stringResource(R.string.media_type_anime)
                    MediaType.MANGA -> stringResource(R.string.media_type_manga)
                },
                ratesList = ratesList,
                itemsCount = ratesList.count,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                    .padding(all = 12.dp)
            )
        }
    }
}