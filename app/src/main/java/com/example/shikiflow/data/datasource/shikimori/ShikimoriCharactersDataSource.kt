package com.example.shikiflow.data.datasource.shikimori

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.graphql.shikimori.AnimeCharactersQuery
import com.example.graphql.shikimori.MangaCharactersQuery
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toDomain
import com.example.shikiflow.data.remote.CharacterApi
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.tracks.MediaType
import javax.inject.Inject

class ShikimoriCharactersDataSource @Inject constructor(
    private val characterApi: CharacterApi,
    private val apolloClient: ApolloClient
): CharactersDataSource {
    override suspend fun getCharacterDetails(
        characterId: Int
    ): Result<MediaCharacter> {
        return try{
            val response = characterApi.getCharacterDetails(characterId.toString()).toDomain()

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    /*val query = CharacterDetailsQuery(
        ids = Optional.presentIfNotNull(listOf(characterId))
    )

    return try {
        val response = apolloClient.query(query).execute()

        response.data?.let { charactersResponse ->
            Result.success(charactersResponse.characters.first())
        } ?: Result.failure(Exception("No data"))
    } catch (e: Exception) {
        Result.failure(e)
    }*/

    override suspend fun loadMediaCharacters(
        page: Int,
        limit: Int,
        mediaId: Int,
        mediaType: MediaType
    ): Result<List<MediaCharacterShort>> {
        return try {
            when(mediaType) {
                MediaType.ANIME -> {
                    val response = apolloClient
                        .query(
                            AnimeCharactersQuery(mediaId.toString())
                        ).execute()

                    val characters = response.data
                        ?.animes
                        ?.first()
                        ?.characterRoles?.map { characterRoles ->
                            characterRoles.shikiCharacterRole.toDomain()
                        } ?: emptyList()

                    Log.d("ShikiCharactersDataSource", "Characters: $characters")
                    Result.success(characters)
                }
                MediaType.MANGA -> {
                    val response = apolloClient
                        .query(
                            MangaCharactersQuery(mediaId.toString())
                        ).execute()

                    val characters = response.data
                        ?.mangas
                        ?.first()
                        ?.characterRoles?.map { characters ->
                            characters.shikiCharacterRole.toDomain()
                        } ?: emptyList()

                    Result.success(characters)
                }
            }
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }
}