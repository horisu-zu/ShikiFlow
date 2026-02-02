package com.example.shikiflow.data.datasource.anilist

import com.apollographql.apollo.ApolloClient
import com.example.graphql.anilist.CharacterDetailsQuery
import com.example.graphql.anilist.CharactersQuery
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.mapper.anilist.AnilistCharacterMapper.toDomain
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.tracks.MediaType
import java.lang.IllegalStateException
import javax.inject.Inject

class AnilistCharactersDataSource @Inject constructor(
    private val apolloClient: ApolloClient
): CharactersDataSource {
    override suspend fun getCharacterDetails(characterId: Int): Result<MediaCharacter> {
        val characterQuery = CharacterDetailsQuery(characterId)

        return try {
            val response = apolloClient.query(characterQuery).execute()

            val result = response.data
                ?.Character
                ?.toDomain()

            result?.let {
                Result.success(result)
            } ?: Result.failure(exception = IllegalStateException("No Data"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadMediaCharacters(
        page: Int,
        limit: Int,
        mediaId: Int,
        mediaType: MediaType
    ): Result<List<MediaCharacterShort>> {
        val charactersQuery = CharactersQuery(mediaId, page, limit)

        return try {
            val response = apolloClient.query(charactersQuery).execute()

            val result = response.data
                ?.Media
                ?.characters
                ?.edges
                ?.mapNotNull { it?.toDomain() }

            result?.let {
                Result.success(result)
            } ?: Result.success(emptyList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}