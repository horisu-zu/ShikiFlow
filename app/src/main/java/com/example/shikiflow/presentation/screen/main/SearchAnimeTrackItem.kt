package com.example.shikiflow.presentation.screen.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.graphql.AnimeBrowseQuery
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.isWatched
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapStatusToString
import com.example.shikiflow.presentation.common.StatusCard
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.utils.StatusColor

@Composable
fun SearchAnimeTrackItem(
    animeItem: AnimeBrowseQuery.Anime,
    onItemClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 24.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onItemClick(animeItem.id) },
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start)
    ) {
        BaseImage(
            model = animeItem.poster?.posterShort?.originalUrl,
            contentDescription = "Poster",
            modifier = Modifier.width(96.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = animeItem.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                //fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                StatusCard(
                    text = mapStatusToString(
                        status = animeItem.userRate?.animeUserRate?.status ?: UserRateStatusEnum.UNKNOWN__
                    ),
                    color = animeItem.userRate?.animeUserRate?.status?.let {
                        StatusColor.getAnimeStatusColor(it)
                    } ?: Color(0xFF8C8C8C)
                )
                if(isWatched(animeItem.userRate?.animeUserRate?.status ?: UserRateStatusEnum.UNKNOWN__)) {
                    StatusCard(
                        text = "Episodes: ${animeItem.userRate?.animeUserRate?.episodes}"
                    )
                }
            }
        }
    }
}