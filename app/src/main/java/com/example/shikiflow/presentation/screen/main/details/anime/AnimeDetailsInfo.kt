package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.graphql.AnimeDetailsQuery
import com.example.graphql.type.AnimeStatusEnum
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapStatusToString
import com.example.shikiflow.presentation.common.CardItem
import com.example.shikiflow.presentation.common.Graph
import com.example.shikiflow.presentation.common.GraphGridType
import com.example.shikiflow.presentation.screen.main.details.common.CommentSection
import com.example.shikiflow.presentation.screen.more.GeneralItem
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.IconResource
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.time.Instant

@Composable
fun AnimeDetailsInfo(
    animeDetails: AnimeDetailsQuery.Anime,
    onLinkClick: (String) -> Unit,
    onSimilarClick: (String, String) -> Unit,
    onExternalLinksClick: (String) -> Unit,
    onEntityClick: (Converter.EntityType, String) -> Unit,
    onTopicNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DetailRow(
            label = stringResource(R.string.details_info_studio),
            verticalAlignment = Alignment.Top,
            content = {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                ) {
                    animeDetails.studios.forEach { CardItem(it.name) }
                }
            }
        )
        if (animeDetails.duration != null && animeDetails.duration != 0) {
            DetailRow(
                label = stringResource(R.string.details_info_duration),
                content = {
                    Text(
                        text = stringResource(
                            R.string.details_info_duration_min,
                            animeDetails.duration
                        ),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            )
        }
        animeDetails.airedOn?.date?.let {
            DetailRow(
                label = stringResource(R.string.details_info_aired_on),
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
                    label = stringResource(R.string.details_info_next_episode_at),
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
                label = stringResource(R.string.details_info_released_on),
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
            label = stringResource(R.string.details_info_title_romaji),
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
                label = stringResource(R.string.details_info_title_japanese),
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
                label = stringResource(R.string.details_info_title_synonyms),
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

        HorizontalDivider()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
            horizontalAlignment = Alignment.Start
        ) {
            GeneralItem(
                icon = IconResource.Drawable(resId = R.drawable.ic_intersection),
                title = stringResource(R.string.details_info_similar),
                onClick = { onSimilarClick(animeDetails.id, animeDetails.name) }
            )
            GeneralItem(
                icon = IconResource.Drawable(resId = R.drawable.ic_link),
                title = stringResource(R.string.details_info_links),
                onClick = { onExternalLinksClick(animeDetails.id) }
            )
        }
        HorizontalDivider()

        if(animeDetails.status != AnimeStatusEnum.anons) {
            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.details_info_score_stats, animeDetails.score ?: 0.0),
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
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.details_info_statuses_stats),
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

        animeDetails.topic?.id?.let { topicId ->
            HorizontalDivider()
            CommentSection(
                topicId = topicId,
                onEntityClick = onEntityClick,
                onTopicNavigate = onTopicNavigate,
                onLinkClick = onLinkClick
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