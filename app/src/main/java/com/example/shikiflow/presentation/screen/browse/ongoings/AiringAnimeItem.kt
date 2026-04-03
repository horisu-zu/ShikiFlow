package com.example.shikiflow.presentation.screen.browse.ongoings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.screen.browse.BrowseCoverItem
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun AiringAnimeItem(
    airingAnime: AiringAnime,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick(airingAnime.data.id) },
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
    ) {
        BrowseCoverItem(
            posterUrl = airingAnime.data.coverImageUrl,
            mediaType = MediaType.ANIME,
            userRateStatus = airingAnime.data.userRateStatus,
            coverWidth = 96.dp,
            cornerShape = 12.dp
        )

        Column(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
        ) {
            Text(
                text = airingAnime.data.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = buildAnnotatedString {
                    airingAnime.airingAt?.let { airingAt ->
                        val time = airingAt.toLocalDateTime(TimeZone.currentSystemDefault()).time

                        append(stringResource(R.string.airing_at, airingAnime.episode))
                        withStyle(
                            style = SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            append(time.toString())
                        }
                    } ?: airingAnime.releasedOn?.let {
                        append(stringResource(R.string.airing_released))
                    }
                },
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}