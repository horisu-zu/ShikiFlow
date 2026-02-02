package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.mapUserRateStatus
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.presentation.common.Graph
import com.example.shikiflow.presentation.common.GraphGridType

@Composable
fun AnimeDetailsStatsComponent(
    isAnnounced: Boolean,
    titleScore: Float?,
    scoreStats: Map<Int, Int>,
    statusesStats: Map<UserRateStatus, Int>
) {
    if(!isAnnounced && titleScore != null) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.details_info_score_stats, titleScore),
                style = MaterialTheme.typography.titleMedium
            )
            Graph(
                data = scoreStats.mapKeys { it.key.toString() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.details_info_statuses_stats),
            style = MaterialTheme.typography.titleMedium
        )
        Graph(
            data = statusesStats.mapKeys { statusEntry ->
                stringResource(id = mapUserRateStatus(statusEntry.key))
            },
            gridType = GraphGridType.VERTICAL,
            modifier = Modifier.fillMaxWidth(),
            height = 180.dp
        )
    }
}