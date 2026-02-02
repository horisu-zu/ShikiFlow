package com.example.shikiflow.data.datasource

import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.tracks.MediaType

interface CharactersDataSource {
    suspend fun getCharacterDetails(
        characterId: Int
    ): Result<MediaCharacter>

    suspend fun loadMediaCharacters(
        page: Int,
        limit: Int,
        mediaId: Int,
        mediaType: MediaType
    ): Result<List<MediaCharacterShort>>
}