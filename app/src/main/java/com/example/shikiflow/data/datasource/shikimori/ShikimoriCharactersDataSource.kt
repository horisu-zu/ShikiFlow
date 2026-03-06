package com.example.shikiflow.data.datasource.shikimori

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.example.graphql.shikimori.AnimeCharactersQuery
import com.example.graphql.shikimori.MangaCharactersQuery
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toCharacterRole
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toDomain
import com.example.shikiflow.data.remote.CharacterApi
import com.example.shikiflow.domain.model.character.CharacterMediaRole
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.AnilistUtils.toResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
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

    override fun getCharacterMediaAppearances(
        characterId: Int,
        mediaType: MediaType
    ): Flow<PagingData<CharacterMediaRole>> {
        return Pager(config = PagingConfig(pageSize = Int.MAX_VALUE)) {
            object : PagingSource<Int, CharacterMediaRole>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterMediaRole> {
                    val detailsResult = getCharacterDetails(characterId)

                    return detailsResult.fold(
                        onSuccess = { details ->
                            delay(200)

                            val mediaRoles = when(mediaType) {
                                MediaType.ANIME -> details.animeRoles.entries
                                MediaType.MANGA -> details.mangaRoles.entries
                            }.map { it.toCharacterRole() }

                            LoadResult.Page(
                                data = mediaRoles,
                                prevKey = null,
                                nextKey = null
                            )
                        },
                        onFailure = { e ->
                            LoadResult.Error(e)
                        }
                    )
                }

                override fun getRefreshKey(state: PagingState<Int, CharacterMediaRole>): Int? = null
            }
        }.flow
    }

    override suspend fun loadMediaCharacters(
        page: Int,
        limit: Int,
        mediaId: Int,
        mediaType: MediaType
    ): Result<List<MediaCharacterShort>> {
        return when(mediaType) {
            MediaType.ANIME -> {
                val response = apolloClient
                    .query(
                        AnimeCharactersQuery(mediaId.toString())
                    ).execute()


                response.toResult().map { data ->
                    data.animes
                        .first()
                        .characterRoles?.map { characterRoles ->
                            characterRoles.shikiCharacterRole.toDomain()
                        } ?: emptyList()
                }
            }
            MediaType.MANGA -> {
                val response = apolloClient
                    .query(
                        MangaCharactersQuery(mediaId.toString())
                    ).execute()

                response.toResult().map { data ->
                    data.mangas
                        .first()
                        .characterRoles?.map { characterRoles ->
                            characterRoles.shikiCharacterRole.toDomain()
                        } ?: emptyList()
                }
            }
        }
    }
}