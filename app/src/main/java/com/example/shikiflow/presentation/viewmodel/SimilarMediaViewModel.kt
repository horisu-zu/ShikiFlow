package com.example.shikiflow.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shikiflow.data.anime.Browse
import com.example.shikiflow.data.anime.toBrowseAnime
import com.example.shikiflow.data.anime.toBrowseManga
import com.example.shikiflow.data.tracks.MediaType
import com.example.shikiflow.domain.repository.AnimeRepository
import com.example.shikiflow.domain.repository.MangaRepository
import com.example.shikiflow.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimilarMediaViewModel @Inject constructor(
    private val animeRepository: AnimeRepository,
    private val mangaRepository: MangaRepository
): ViewModel() {

    private val _lastMediaId = MutableStateFlow<String?>(null)
    private val _similarMedia = MutableStateFlow<Resource<List<Browse>>>(Resource.Loading())
    val similarMedia = _similarMedia.asStateFlow()

    fun getSimilarMedia(
        mediaId: String,
        mediaType: MediaType
    ) {
        viewModelScope.launch {
            if(mediaId == _lastMediaId.value) {
                return@launch
            }
            _similarMedia.value = Resource.Loading()
            when (mediaType) {
                MediaType.ANIME -> {
                    val response = animeRepository.getSimilarAnime(mediaId).map { result ->
                        result.toBrowseAnime()
                    }
                    _similarMedia.value = Resource.Success(response)
                }
                MediaType.MANGA -> {
                    val response = mangaRepository.getSimilarManga(mediaId).map { result ->
                        result.toBrowseManga()
                    }
                    _similarMedia.value = Resource.Success(response)
                }
            }
            _lastMediaId.value = mediaId
        }
    }
}