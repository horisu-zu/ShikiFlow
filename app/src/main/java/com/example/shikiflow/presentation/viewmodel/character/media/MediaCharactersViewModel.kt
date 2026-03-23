package com.example.shikiflow.presentation.viewmodel.character.media

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MediaCharactersViewModel @Inject constructor(
    private val charactersRepository: CharacterRepository
): ViewModel() {

    private val _params = MutableStateFlow(MediaCharactersParams())

    val mediaCharacters = _params
        .filter { state ->
            state.mediaId != null && state.mediaType != null
        }
        .distinctUntilChanged()
        .flatMapLatest { state ->
            charactersRepository.getMediaCharacters(state.mediaId!!, state.mediaType!!)
        }.cachedIn(viewModelScope)

    fun setParams(mediaId: Int, mediaType: MediaType) {
        _params.update { state ->
            state.copy(
                mediaId = mediaId,
                mediaType = mediaType
            )
        }
    }
}