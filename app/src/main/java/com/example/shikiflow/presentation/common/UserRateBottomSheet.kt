package com.example.shikiflow.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.mapper.UserRateMapper.Companion.simpleMapUserRateStatusToString
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRateData
import com.example.shikiflow.domain.model.mapper.UserRateStatusConstants
import com.example.shikiflow.domain.model.tracks.RateStatus
import com.example.shikiflow.presentation.common.image.RoundedImage
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.toIcon
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRateBottomSheet(
    userRate: UserRateData,
    onDismiss: () -> Unit,
    onSave: (Long, Int, Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    onCreateRate: (String, Int) -> Unit = { _, _ -> },
    isLoading: Boolean = false
) {
    val sheetState = rememberModalBottomSheetState()
    val chips = UserRateStatusConstants.getStatusChips(userRate.mediaType)
    val initialStatusIndex = chips.indexOfFirst { chip ->
        chip == simpleMapUserRateStatusToString(userRate.status, userRate.mediaType)
    }

    var selectedStatus by remember { mutableIntStateOf(initialStatusIndex) }
    var selectedScore by remember { mutableIntStateOf(userRate.score) }
    var progress by remember { mutableIntStateOf(userRate.progress) }
    var rewatches by remember { mutableIntStateOf(userRate.rewatches) }

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            SheetHeader(
                posterUrl = userRate.posterUrl ?: "",
                title = userRate.title,
                onDismiss = onDismiss
            )

            StatusChips(
                chips = chips.map { resId -> stringResource(id = resId) },
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it }
            )

            if(userRate.id != null) {
                ScoreSelector(
                    score = selectedScore,
                    onScoreChange = { selectedScore = it }
                )

                ProgressColumn(
                    userRate = userRate.copy(
                        progress = progress,
                        rewatches = rewatches
                    ),
                    onProgressChange = { newProgress -> progress = newProgress },
                    onRewatchesChange = { newRewatches -> rewatches = newRewatches }
                )

                ChangeRow(
                    onSave = {
                        onSave(
                            userRate.id.toLong(),
                            selectedStatus,
                            selectedScore,
                            progress,
                            rewatches
                        )
                    },
                    createDate = userRate.createDate,
                    updateDate = userRate.updateDate,
                    isLoading = isLoading,
                    enabled = !isLoading
                )
            } else {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    label = stringResource(R.string.user_rate_add_to_list),
                    onClick = { onCreateRate(userRate.mediaId, selectedStatus) },
                    enabled = selectedStatus != -1
                )
            }
        }
    }
}

@Composable
private fun SheetHeader(
    posterUrl: String,
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RoundedImage(
            model = posterUrl,
            clip = RoundedCornerShape(8.dp),
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(48.dp)
        )
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.rate_progress),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        IconButton(onClick = onDismiss) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun StatusChips(
    chips: List<String>,
    selectedStatus: Int,
    onStatusSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val statusMap = RateStatus.entries.associateBy {
        it.name.lowercase().replace("_", " ")
    }

    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chips) { tab ->
            FilterChip(
                selected = selectedStatus != -1 && chips.getOrNull(selectedStatus) == tab,
                onClick = {
                    val newIndex = chips.indexOf(tab)
                    onStatusSelected(if (selectedStatus == newIndex) -1 else newIndex)
                },
                label = { Text(tab) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                leadingIcon = {
                    statusMap[tab.lowercase()]?.icon?.toIcon(
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                    )
                }
            )
        }
    }
}

@Composable
private fun ScoreSelector(
    score: Int,
    onScoreChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            value = score.toFloat(),
            onValueChange = { onScoreChange(it.toInt()) },
            steps = 9,
            valueRange = 0f..10f,
            modifier = Modifier
                .height(24.dp)
                .weight(1f)
        )
        Text(
            text = score.toString(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ProgressColumn(
    userRate: UserRateData,
    onProgressChange: (Int) -> Unit,
    onRewatchesChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAnime = userRate.mediaType == MediaType.ANIME
    val totalCount = if (isAnime) userRate.totalEpisodes else userRate.totalChapters
    val progressTitle = if (isAnime) stringResource(id = R.string.details_short_info_episodes)
        else stringResource(id = R.string.details_short_info_manga_chapters)
    val rewatchTitle = if (isAnime) stringResource(R.string.rewatches)
        else stringResource(R.string.rereads)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        totalCount?.let {
            ProgressCard(
                title = progressTitle,
                count = userRate.progress,
                onIncrement = { onProgressChange(userRate.progress + 1) },
                onDecrement = { onProgressChange(userRate.progress - 1) },
                canIncrement = userRate.progress < totalCount,
                canDecrement = userRate.progress > 0
            )
        }
        ProgressCard(
            title = rewatchTitle,
            count = userRate.rewatches,
            onIncrement = { onRewatchesChange(userRate.rewatches + 1) },
            onDecrement = { onRewatchesChange(userRate.rewatches - 1) },
            canIncrement = true,
            canDecrement = userRate.rewatches > 0
        )
    }
}

@Composable
private fun ProgressCard(
    title: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    canIncrement: Boolean,
    canDecrement: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            //text = "$title — $count",
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Medium
                )) {
                    append(title)
                }
                append(" — ")
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold
                )) {
                    append(count.toString())
                }
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.weight(1f)
        )
        RoundBox(
            text = stringResource(R.string.rate_decrement),
            onClick = onDecrement,
            enabled = canDecrement
        )
        RoundBox(
            text = stringResource(R.string.rate_increment),
            onClick = onIncrement,
            enabled = canIncrement
        )
    }
}

@Composable
private fun RoundBox(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                if (enabled) MaterialTheme.colorScheme.surface
                else MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = if (enabled)
                MaterialTheme.colorScheme.onSurface
            else
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}


@Composable
private fun ChangeRow(
    onSave: () -> Unit,
    createDate: Instant,
    updateDate: Instant,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            DateItem(
                iconVector = Icons.Default.Add,
                date = createDate
            )
            DateItem(
                iconVector = Icons.Default.Edit,
                date = updateDate
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surface)
                .size(48.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                IconButton(
                    onClick = onSave,
                    enabled = enabled
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun DateItem(
    iconVector: ImageVector,
    date: Instant
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = Converter.formatInstant(date, includeTime = true),
            style = MaterialTheme.typography.labelMedium
        )
    }
}