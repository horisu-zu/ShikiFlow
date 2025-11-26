package com.example.shikiflow.presentation.screen.main.details.anime.watch.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    isLoading: Boolean,
    isPreviousAvailable: Boolean,
    isNextAvailable: Boolean,
    onPlay: () -> Unit,
    onSeekToEpisode: (Int) -> Unit,
    showControls: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(36.dp, Alignment.CenterHorizontally)
    ) {
        ControlButton(
            visible = showControls,
            enabled = isPreviousAvailable,
            onClick = { onSeekToEpisode(-1) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_previous),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color.White.copy(alpha = if(isPreviousAvailable) 1f else 0.35f)
            )
        }
        if(isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        } else {
            ControlButton(
                visible = showControls,
                onClick = onPlay
            ) {
                (if(isPlaying) {
                    IconResource.Drawable(R.drawable.ic_pause)
                } else {
                    IconResource.Vector(Icons.Default.PlayArrow)
                }).toIcon(
                    modifier = Modifier.size(48.dp),
                    tint = Color.White
                )
            }
        }
        ControlButton(
            visible = showControls,
            enabled = isNextAvailable,
            onClick = { onSeekToEpisode(1) }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Color.White.copy(alpha = if(isNextAvailable) 1f else 0.35f)
            )
        }
    }
}

@Composable
private fun ControlButton(
    visible: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        IconButton(
            enabled = enabled,
            onClick = onClick
        ) {
            content()
        }
    }
}