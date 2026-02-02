package com.example.shikiflow.presentation.viewmodel.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MediaCharactersViewModel @Inject constructor(
    private val charactersRepository: CharacterRepository
): ViewModel() {

    private var _charactersMap = mutableMapOf<Int, Flow<PagingData<MediaCharacterShort>>>()

    fun getMediaCharacters(
        mediaId: Int,
        mediaType: MediaType
    ): Flow<PagingData<MediaCharacterShort>> {
        return _charactersMap.getOrPut(mediaId) {
            charactersRepository.getMediaCharacters(mediaId, mediaType).cachedIn(viewModelScope)
        }
    }
}