package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.utils.Converter

@Composable
fun PlayerBottomComponent(
    currentProgress: Long,
    duration: Long,
    isFit: Boolean,
    onSeek: (Long) -> Unit,
    onSkip: () -> Unit,
    onResize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val thumbSize = 16.dp

    Row(
        modifier = modifier.fillMaxWidth()
            /*.background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                )
            )*/
            .padding(start = 48.dp, bottom = 16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
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
                Text(
                    text = Converter.formatDuration(currentProgress),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    text = Converter.formatDuration(duration),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
        IconButton(onClick = onSkip) {
            Icon(
                painter = painterResource(id = R.drawable.ic_double_arrow),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = onResize) {
            Icon(
                painter = painterResource(
                    id = if(isFit) R.drawable.ic_exit_full_screen else R.drawable.ic_open_full_screen
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
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