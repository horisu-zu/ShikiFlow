package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun getCharacterDetails(
        characterId: Int
    ): Result<MediaCharacter>

    fun getMediaCharacters(
        mediaId: Int,
        mediaType: MediaType
    ): Flow<PagingData<MediaCharacterShort>>
}