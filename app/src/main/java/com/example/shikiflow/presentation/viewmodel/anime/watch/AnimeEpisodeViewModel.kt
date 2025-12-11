package com.example.shikiflow.presentation.viewmodel.anime.watch

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.shikiflow.domain.model.kodik.KodikEpisode
import com.example.shikiflow.domain.usecase.GetEpisodesUseCase
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
@UnstableApi
class AnimeEpisodeViewModel @Inject constructor(
    private val getEpisodesUseCase: GetEpisodesUseCase
) : ViewModel() {

    private val _episodeState = MutableStateFlow<Resource<KodikEpisode>>(Resource.Loading())
    val episodeState = _episodeState.asStateFlow()

    var currentQuality by mutableStateOf<String>("")
        private set

    private val _mediaSource = MutableStateFlow<MediaSource?>(null)
    val mediaSource = _mediaSource.asStateFlow()

    fun getEpisode(link: String, serialNum: Int) {
        getEpisodesUseCase(
            url = link,
            serialNum = serialNum
        ).onEach { result ->
            _episodeState.value = result
            when(result) {
                is Resource.Loading -> {
                    Log.d("AnimeEpisodeViewModel","Loading episode with URL: $link")
                }
                is Resource.Success -> {
                    result.data?.let { kodikLink ->
                        _mediaSource.value = createMediaSource(kodikLink.qualityLink)
                    }
                    Log.d("AnimeEpisodeViewModel","Result: ${result.data}")
                }
                is Resource.Error -> {
                    Log.d("AnimeEpisodeViewModel", "Error: ${result.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    fun clearMediaSource() {
        _mediaSource.value = null
    }

    private fun createMediaSource(qualityLinks: Map<String, String>): MediaSource? {
        val videoUrl = qualityLinks.maxBy { it.key.toInt() }.value
        Log.d("AnimeEpisodeViewModel", "Quality Links: $qualityLinks")
        currentQuality = extractQualityFromUrl(videoUrl)
        return createMediaSourceFromUrl(videoUrl)
    }

    fun createMediaSource(quality: String) {
        val episodeData = _episodeState.value.data?.qualityLink ?: return
        val videoUrl = episodeData[quality] ?: return
        currentQuality = quality

        _mediaSource.value = createMediaSourceFromUrl(videoUrl)
    }

    private fun createMediaSourceFromUrl(videoUrl: String): MediaSource? {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        return when {
            videoUrl.contains("m3u8") -> {
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(videoUrl))
            }
            else -> {
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(videoUrl))
            }
        }
    }

    private fun extractQualityFromUrl(videoUrl: String): String {
        return videoUrl
            .substringAfterLast("/")
            .substringBefore(":")
            .substringBefore(".mp4")
            .takeIf { it.matches(Regex("\\d+p?")) } ?: "unknown"
    }
}