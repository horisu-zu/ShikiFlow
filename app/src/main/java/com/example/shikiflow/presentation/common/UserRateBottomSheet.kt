package com.example.shikiflow.presentation.common

import android.os.Build
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.domain.model.tracks.RateUpdateState
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserRateData
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.SaveUserRate
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.floatingPointRange
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.formatValue
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.steps
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.valueRange
import com.example.shikiflow.presentation.common.mappers.UserRateIconProvider.icon
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus
import com.example.shikiflow.utils.Converter
import com.example.shikiflow.utils.toIcon
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRateBottomSheet(
    userRate: UserRateData,
    rateUpdateState: RateUpdateState,
    preferredTitleType: PreferredTitleType,
    scoreFormat: ScoreFormat,
    onDismiss: () -> Unit,
    onSave: (SaveUserRate) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val scope = rememberCoroutineScope()

    val chips = UserRateStatus.entries.filter { it != UserRateStatus.UNKNOWN }.toList()
    val initialStatusIndex = chips.indexOfFirst { chip ->
        chip == userRate.status
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    var selectedStatus by remember { mutableIntStateOf(initialStatusIndex) }
    var selectedScore by remember {
        mutableFloatStateOf(scoreFormat.formatValue(userRate.score.toFloat()))
    }
    var progress by remember { mutableIntStateOf(userRate.progress) }
    var progressVolumes by remember { mutableIntStateOf(userRate.progressVolumes) }
    var rewatches by remember { mutableIntStateOf(userRate.rewatches) }

    LaunchedEffect(rateUpdateState) {
        if(sheetState.isVisible && rateUpdateState == RateUpdateState.FINISHED) {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                onDismiss()
            }
        }
    }

    if(showDeleteDialog) {
        CustomDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = stringResource(R.string.user_rate_delete),
            confirmButtonText = stringResource(R.string.common_ok),
            onConfirm = { onDelete(userRate.id ?: 0) }
        )
    }

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = { onDismiss() }
    ) {
        val horizontalPadding = 16.dp
        (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
            }
        }
        Column(
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            SheetHeader(
                posterUrl = userRate.posterUrl,
                title = userRate.title.preferred(preferredTitleType),
                onDismiss = onDismiss
            )

            StatusChips(
                chips = chips,
                mediaType = userRate.mediaType,
                selectedStatus = selectedStatus,
                onStatusSelected = { selectedStatus = it },
                horizontalPadding = horizontalPadding
            )

            AnimatedVisibility(
                visible = selectedStatus != -1
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                ) {
                    ScoreSelector(
                        score = selectedScore,
                        scoreFormat = scoreFormat,
                        onScoreChange = { selectedScore = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ProgressColumn(
                        userRate = userRate.copy(
                            progress = progress,
                            progressVolumes = progressVolumes,
                            rewatches = rewatches
                        ),
                        onProgressChange = { progress = it },
                        onVolumesProgressChange = { progressVolumes = it },
                        onRewatchesChange = { rewatches = it }
                    )
                }
            }
            if(userRate.id != null) {
                ChangeRow(
                    onSave = {
                        onSave(
                            SaveUserRate(
                                rateId = userRate.id,
                                mediaId = userRate.mediaId,
                                malId = userRate.malId,
                                userStatus = UserRateStatus.entries[selectedStatus],
                                score = selectedScore,
                                progress  = progress,
                                progressVolumes = progressVolumes,
                                repeat = rewatches
                            )
                        )
                    },
                    onDelete = { showDeleteDialog = true },
                    createDate = userRate.createDate,
                    updateDate = userRate.updateDate,
                    isLoading = rateUpdateState == RateUpdateState.LOADING,
                    enabled = rateUpdateState != RateUpdateState.LOADING
                )
            } else {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp)),
                    label = stringResource(R.string.user_rate_add_to_list),
                    onClick = {
                        onSave(
                            SaveUserRate(
                                mediaId = userRate.mediaId,
                                malId = userRate.malId,
                                userStatus = UserRateStatus.entries[selectedStatus],
                                score = selectedScore,
                                progress  = progress,
                                progressVolumes = progressVolumes,
                                repeat = rewatches
                            )
                        )
                    },
                    enabled = selectedStatus != -1
                )
            }
        }
    }
}

@Composable
private fun SheetHeader(
    posterUrl: String?,
    title: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BaseImage(
            model = posterUrl,
            imageType = ImageType.Square(
                width = 48.dp,
                shape = RoundedCornerShape(8.dp)
            )
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
    chips: List<UserRateStatus>,
    mediaType: MediaType,
    selectedStatus: Int,
    onStatusSelected: (Int) -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.ignoreHorizontalParentPadding(horizontalPadding),
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(chips) { userRateStatus ->
            val rateStatus = userRateStatus.mapStatus(mediaType)

            FilterChip(
                selected = selectedStatus != -1 && chips.getOrNull(selectedStatus) == userRateStatus,
                onClick = {
                    val newIndex = chips.indexOf(userRateStatus)
                    onStatusSelected(if (selectedStatus == newIndex) -1 else newIndex)
                },
                label = {
                    Text(
                        text = stringResource(rateStatus)
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                leadingIcon = {
                    userRateStatus.icon(mediaType).toIcon(
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
    score: Float,
    scoreFormat: ScoreFormat,
    onScoreChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    when(scoreFormat) {
        ScoreFormat.POINT_100,
        ScoreFormat.POINT_10_DECIMAL,
        ScoreFormat.POINT_10 -> {
            ScoreSlider(
                score = score,
                scoreFormat = scoreFormat,
                onScoreChange = onScoreChange,
                modifier = modifier
            )
        }
        ScoreFormat.POINT_5 -> {
            StarScore(
                score = score,
                onScoreChange = onScoreChange,
                modifier = modifier
            )
        }
        ScoreFormat.POINT_3 -> {
            SmileyScore(
                score = score,
                onScoreChange = onScoreChange,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun ScoreSlider(
    score: Float,
    scoreFormat: ScoreFormat,
    onScoreChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Slider(
            value = score,
            onValueChange = { value ->
                val step = scoreFormat.valueRange().step
                val rounded = (value / step).roundToInt() * step

                onScoreChange(rounded)
            },
            steps = scoreFormat.valueRange().steps(),
            valueRange = scoreFormat.valueRange().floatingPointRange(),
            modifier = Modifier
                .height(24.dp)
                .weight(1f)
        )

        Text(
            text = scoreFormat.displayValue(score),
            style = MaterialTheme.typography.bodyLarge.copy(
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            ),
            modifier = Modifier
                .width(48.dp)
                .clip(RoundedCornerShape(percent = 24))
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .padding(all = 8.dp)
        )
    }
}

@Composable
private fun StarScore(
    score: Float,
    onScoreChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(5) { index ->
            val isSelected = score.roundToInt() >= index + 1

            IconButton(
                onClick = {
                    val newScore = index + 1f
                    onScoreChange(
                        if (newScore == score) 0f
                            else newScore
                    )
                },
                shape = RoundedCornerShape(percent = 24)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = when (isSelected) {
                        true -> MaterialTheme.colorScheme.secondary
                        false -> MaterialTheme.colorScheme.surfaceBright
                    },
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun SmileyScore(
    score: Float,
    onScoreChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(score) {
        Log.d("SmileyScore", "Score: $score")
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val isSelected = score.roundToInt() >= index + 1

            IconButton(
                onClick = {
                    val newScore = index + 1f
                    onScoreChange(
                        if (newScore == score) 0f
                            else newScore
                    )
                },
                shape = RoundedCornerShape(percent = 24)
            ) {
                Icon(
                    painter = when (index) {
                        2 -> painterResource(R.drawable.ic_satisfied)
                        1 -> painterResource(R.drawable.ic_neutral)
                        else -> painterResource(R.drawable.ic_dissatisfied)
                    },
                    contentDescription = null,
                    tint = when (isSelected) {
                        true -> MaterialTheme.colorScheme.secondary
                        false -> MaterialTheme.colorScheme.surfaceBright
                    },
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Composable
private fun ProgressColumn(
    userRate: UserRateData,
    onProgressChange: (Int) -> Unit,
    onVolumesProgressChange: (Int) -> Unit,
    onRewatchesChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isAnime = userRate.mediaType == MediaType.ANIME
    val progressTitle = if (isAnime) stringResource(id = R.string.details_short_info_episodes)
        else stringResource(id = R.string.details_short_info_manga_chapters)
    val rewatchTitle = if (isAnime) stringResource(R.string.rewatches)
        else stringResource(R.string.rereads)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        ProgressCard(
            title = progressTitle,
            count = userRate.progress,
            onIncrement = { onProgressChange(userRate.progress + 1) },
            onDecrement = { onProgressChange(userRate.progress - 1) },
            canIncrement = userRate.progress < userRate.totalCount,
            canDecrement = userRate.progress > 0
        )
        if(!isAnime) {
            ProgressCard(
                title = stringResource(R.string.details_volumes),
                count = userRate.progressVolumes,
                onIncrement = { onVolumesProgressChange(userRate.progressVolumes + 1) },
                onDecrement = { onVolumesProgressChange(userRate.progressVolumes - 1) },
                canIncrement = userRate.progressVolumes < userRate.volumesCount,
                canDecrement = userRate.progressVolumes > 0
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
            .clip(RoundedCornerShape(percent = 24))
            .background(
                color = if (enabled) MaterialTheme.colorScheme.surface
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
    onDelete: () -> Unit,
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
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            DateItem(
                iconVector = Icons.Default.Add,
                date = createDate
            )
            DateItem(
                iconVector = Icons.Default.Edit,
                date = updateDate
            )
        }

        IconButton(
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                tint = MaterialTheme.colorScheme.error,
                contentDescription = "Delete User Rate"
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
                        imageVector = Icons.Outlined.Done,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
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