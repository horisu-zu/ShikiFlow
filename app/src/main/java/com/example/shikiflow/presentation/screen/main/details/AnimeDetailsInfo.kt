package com.example.shikiflow.presentation.screen.main.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.utils.Converter.formatInstant
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

@Composable
fun AnimeDetailsInfo(
    animeDetails: AnimeDetailsQuery.Anime,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        DetailRow(
            label = "Studio",
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
                ) {
                    animeDetails.studios.forEach {
                        CardItem(it.name)
                    }
                }
            }
        )
        if (animeDetails.duration != null) {
            DetailRow(
                label = "Duration",
                content = {
                    Text(
                        text = "${animeDetails.duration} min."
                    )
                }
            )
        }
        DetailRow(
            label = "Aired On",
            content = {
                Text(
                    text = formatInstant(
                        LocalDate.parse(animeDetails.airedOn?.date.toString())
                            .atStartOfDayIn(TimeZone.currentSystemDefault()),
                        includeTime = false
                    )
                )
            }
        )
        if (animeDetails.status == AnimeStatusEnum.ongoing) {
            DetailRow(
                label = "Next Episode at",
                content = {
                    Text(
                        text = formatInstant(
                            Instant.parse(animeDetails.nextEpisodeAt.toString()),
                            includeTime = true
                        )
                    )
                }
            )
        } else if (animeDetails.status == AnimeStatusEnum.released && animeDetails.releasedOn?.date != null) {
            DetailRow(
                label = "Released on",
                content = {
                    Text(
                        text = formatInstant(
                            LocalDate.parse(animeDetails.releasedOn.date.toString())
                                .atStartOfDayIn(TimeZone.currentSystemDefault()),
                            includeTime = false
                        )
                    )
                }
            )
        }
        HorizontalDivider()
        DetailRow(
            label = "Romaji",
            content = {
                Text(
                    text = animeDetails.name
                )
            }
        )
        if (animeDetails.japanese != null) {
            DetailRow(
                label = "Japanese",
                content = {
                    Text(
                        text = animeDetails.japanese
                    )
                }
            )
        }
        if (animeDetails.synonyms.isNotEmpty()) {
            DetailRow(
                label = "Synonyms",
                content = {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                    ) {
                        animeDetails.synonyms.forEach { synonym ->
                            Text(
                                text = synonym
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.labelMedium
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )

        ProvideTextStyle(value = style) {
            content()
        }
    }
}