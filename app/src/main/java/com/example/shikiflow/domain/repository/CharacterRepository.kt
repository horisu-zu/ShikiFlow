package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {
    suspend fun getCharacterDetails(
        characterId: Int
    ): Flow<DataResult<MediaCharacter>>

    fun getCharacterMediaRoles(
        characterId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Flow<PagingData<MediaRole>>

    fun getMediaCharacters(
        mediaId: Int,
        mediaType: MediaType
    ): Flow<PagingData<MediaCharacterShort>>

    fun searchCharacters(
        search: String
    ): Flow<PagingData<Browse.Character>>
}