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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.TargetType
import com.example.shikiflow.domain.model.tracks.UserRate
import com.example.shikiflow.utils.Converter.groupAndSortByStatus
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun TrackSection(
    isCurrentUser: Boolean,
    userRateData: List<UserRate>,
    onCompareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animeTrackData = userRateData.filter { it.targetType == TargetType.ANIME }
    val mangaTrackData = userRateData.filter { it.targetType == TargetType.MANGA }

    val groupedAnimeData = animeTrackData.groupAndSortByStatus(MediaType.ANIME)
    val groupedMangaData = mangaTrackData.groupAndSortByStatus(MediaType.MANGA)

    val horizontalPadding = 12.dp

    Column(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = horizontalPadding, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.profile_screen_track_lists_label),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if(!isCurrentUser) {
                Row(
                    modifier = Modifier.clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface)
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

        TrackItem(
            iconResource = IconResource.Drawable(R.drawable.ic_anime),
            type = stringResource(R.string.main_track_mode_anime),
            groupedData = groupedAnimeData.mapKeys { (resId, size) ->
                stringResource(resId)
            },
            itemsCount = animeTrackData.size
        )

        HorizontalDivider(modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding))

        TrackItem(
            iconResource = IconResource.Drawable(R.drawable.ic_manga),
            type = stringResource(R.string.main_track_mode_manga),
            groupedData = groupedMangaData.mapKeys { (resId, size) ->
                stringResource(resId)
            },
            itemsCount = mangaTrackData.size
        )
    }
}