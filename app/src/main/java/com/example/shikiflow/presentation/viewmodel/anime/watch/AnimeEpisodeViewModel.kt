package com.example.shikiflow.presentation.viewmodel.anime.watch

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.example.shikiflow.domain.model.kodik.KodikLink
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

    private val _episodeState = MutableStateFlow<Resource<KodikLink>>(Resource.Loading())
    val episodeState = _episodeState.asStateFlow()

    private val _mediaSource = MutableStateFlow<MediaSource?>(null)
    val mediaSource = _mediaSource.asStateFlow()

    fun getEpisode(link: String, serialNum: Int) {
        getEpisodesUseCase(link, serialNum).onEach { result ->
            _episodeState.value = result
            when(result) {
                is Resource.Loading -> {
                    Log.d("AnimeEpisodesViewModel","Loading episode with URL: $link")
                }
                is Resource.Success -> {
                    result.data?.let { kodikLink ->
                        _mediaSource.value = createMediaSource(kodikLink.qualityLink)
                    }
                    Log.d("AnimeEpisodesViewModel","Result: ${result.data}")
                }
                is Resource.Error -> {
                    Log.d("AnimeEpisodesViewModel", "Error: ${result.message}")
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun createMediaSource(qualityLinks: Map<String, String>): MediaSource? {
        val videoUrl = qualityLinks.maxBy { it.key.toInt() }.value

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
}