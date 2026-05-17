package com.example.shikiflow.presentation.common.player

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer

val LocalExoPlayerCache = staticCompositionLocalOf<ExoPlayerCache> {
    error("No ExoPlayerCache provided")
}

class ExoPlayerCache internal constructor(private val context: Context) {
    private val maxBufferSize = 6

    private val players = object : LinkedHashMap<String, ExoPlayer>(maxBufferSize, 0.75f, true) {
        override fun removeEldestEntry(eldest: Map.Entry<String?, ExoPlayer?>?): Boolean {
            val evict = size > maxBufferSize

            if(evict) {
                eldest?.value?.release()
            }

            return evict
        }
    }

    @OptIn(UnstableApi::class)
    fun getOrCreate(url: String): ExoPlayer {
        return players.getOrPut(url) {
            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    1500, //minBufferMs
                    3000, //maxBufferMs
                    500, //bufferForPlaybackMs
                    1000 //bufferForPlaybackAfterRebufferMs
                )
                .setTargetBufferBytes(1.5.toInt() * 1024 * 1024)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build()

            ExoPlayer.Builder(context)
                .setLoadControl(loadControl)
                .build()
                .apply {
                    setMediaItem(MediaItem.fromUri(url))
                    repeatMode = Player.REPEAT_MODE_ONE
                    playWhenReady = false
                }
        }
    }

    fun releaseAll() {
        players.values.forEach { player ->
            player.release()
        }
        players.clear()
    }
}

@Composable
fun rememberExoPlayerCache(): ExoPlayerCache {
    val context = LocalContext.current.applicationContext
    val cache = remember { ExoPlayerCache(context) }

    DisposableEffect(Unit) {
        onDispose { cache.releaseAll() }
    }

    return cache
}