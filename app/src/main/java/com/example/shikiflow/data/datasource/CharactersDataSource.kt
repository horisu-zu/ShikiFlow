package com.example.shikiflow.data.datasource

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.character.CharacterMediaRole
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlinx.coroutines.flow.Flow

interface CharactersDataSource {
    suspend fun getCharacterDetails(
        characterId: Int
    ): Result<MediaCharacter>

    fun getCharacterMediaAppearances(
        characterId: Int,
        mediaType: MediaType
    ): Flow<PagingData<CharacterMediaRole>>

    suspend fun loadMediaCharacters(
        page: Int,
        limit: Int,
        mediaId: Int,
        mediaType: MediaType
    ): Result<List<MediaCharacterShort>>
}