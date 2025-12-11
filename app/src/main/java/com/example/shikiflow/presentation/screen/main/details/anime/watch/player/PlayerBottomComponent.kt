package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.utils.Converter

@Composable
fun PlayerBottomComponent(
    currentProgress: Long,
    duration: Long,
    shouldShowSkipOp: Boolean,
    isFit: Boolean,
    onSkipOp: () -> Unit,
    onSeek: (Long) -> Unit,
    onSkip: () -> Unit,
    onResize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbSize = 16.dp

    Column(
        modifier = modifier.padding(start = 48.dp, bottom = 16.dp)
    ) {
        AnimatedVisibility(
            visible = shouldShowSkipOp,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row(
                modifier = Modifier.clip(CircleShape)
                    .background(colorResource(R.color.blue))
                    .clickable { onSkipOp() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_double_arrow),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
                Text(
                    text = stringResource(R.string.player_skip_opening_label),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White
                    )
                )
            }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                PlayerSlider(
                    duration = duration,
                    currentProgress = currentProgress,
                    onSeek = onSeek,
                    thumbSize = thumbSize
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = thumbSize / 2),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DurationBox(currentProgress)
                    DurationBox(duration)
                }
            }
            IconButton(onClick = onSkip) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_double_arrow),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
            IconButton(onClick = onResize) {
                Icon(
                    painter = painterResource(
                        id = if(isFit) R.drawable.ic_exit_full_screen else R.drawable.ic_open_full_screen
                    ),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
private fun DurationBox(
    durationMs: Long,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = Converter.formatDuration(durationMs),
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color.White
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlayerSlider(
    duration: Long,
    currentProgress: Long,
    onSeek: (Long) -> Unit,
    thumbSize: Dp,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableFloatStateOf(0f) }
    var isSeeking by remember { mutableStateOf(false) }

    LaunchedEffect(currentProgress, duration) {
        if (!isSeeking && duration > 0) {
            sliderPosition = currentProgress / duration.toFloat()
        }
    }

    Slider(
        value = sliderPosition,
        onValueChange = { newValue ->
            isSeeking = true
            sliderPosition = newValue
        },
        onValueChangeFinished = {
            val newPosition = (duration * sliderPosition).toLong()
            onSeek(newPosition)
            isSeeking = false
        },
        thumb = {
            SliderDefaults.Thumb(
                thumbSize = DpSize(thumbSize, thumbSize),
                interactionSource = remember { MutableInteractionSource() }
            )
        },
        track = { state ->
            SliderDefaults.Track(
                sliderState = state,
                thumbTrackGapSize = 0.dp,
                modifier = Modifier.height(6.dp)
            )
        },
        valueRange = 0f..1f,
        modifier = modifier.fillMaxWidth()
    )
}