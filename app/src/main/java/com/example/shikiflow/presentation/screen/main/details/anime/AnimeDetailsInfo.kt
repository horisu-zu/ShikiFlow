package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.data.mapper.UserRateMapper.Companion.mapStatusToString
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.Graph
import com.example.shikiflow.presentation.common.GraphGridType
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DetailRow(
            label = "Studio",
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End)
                ) {
                    animeDetails.studios.forEach {
                        CardItem(it.name, modifier.padding(6.dp))
                    }
                }
            }
        )
        if (animeDetails.duration != null && animeDetails.duration != 0) {
            DetailRow(
                label = "Duration",
                content = {
                    Text(
                        text = "${animeDetails.duration} min.",
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            )
        }
        animeDetails.airedOn?.date?.let {
            DetailRow(
                label = "Aired On",
                content = {
                    Text(
                        text = formatInstant(
                            LocalDate.parse(it.toString())
                                .atStartOfDayIn(TimeZone.currentSystemDefault()),
                            includeTime = false
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            )
        }
        if (animeDetails.status == AnimeStatusEnum.ongoing) {
            animeDetails.nextEpisodeAt?.let { nextEpisode ->
                DetailRow(
                    label = "Next Episode at",
                    content = {
                        Text(
                            text = formatInstant(
                                Instant.parse(nextEpisode.toString()),
                                includeTime = true
                            ),
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                )
            }
        } else if (animeDetails.status == AnimeStatusEnum.released && animeDetails.releasedOn?.date != null) {
            DetailRow(
                label = "Released on",
                content = {
                    Text(
                        text = formatInstant(
                            LocalDate.parse(animeDetails.releasedOn.date.toString())
                                .atStartOfDayIn(TimeZone.currentSystemDefault()),
                            includeTime = false
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            )
        }
        HorizontalDivider()
        DetailRow(
            label = "Romaji",
            content = {
                Text(
                    text = animeDetails.name,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
        )
        if (animeDetails.japanese != null) {
            DetailRow(
                label = "Japanese",
                content = {
                    Text(
                        text = animeDetails.japanese,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 12.dp)
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
                                text = synonym,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }
                    }
                }
            )
        }

        if(animeDetails.status != AnimeStatusEnum.anons) {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Score stats ∙ ${animeDetails.score} ★",
                    style = MaterialTheme.typography.titleMedium
                )
                Graph(
                    data = animeDetails.scoresStats?.associate {
                        it.score.toString() to it.count.toFloat()
                    } ?: emptyMap(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        Column(
            modifier = modifier.fillMaxWidth().padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Statuses stats",
                style = MaterialTheme.typography.titleMedium
            )
            Graph(
                data = animeDetails.statusesStats?.associate {
                    mapStatusToString(it.status) to it.count.toFloat()
                } ?: emptyMap(),
                gridType = GraphGridType.VERTICAL,
                modifier = Modifier.fillMaxWidth(),
                height = 180.dp
            )
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    style: TextStyle = MaterialTheme.typography.labelMedium
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = verticalAlignment
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