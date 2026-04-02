package com.example.shikiflow.data.datasource.shikimori

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.example.graphql.shikimori.AnimeCharactersQuery
import com.example.graphql.shikimori.CharacterSearchQuery
import com.example.graphql.shikimori.MangaCharactersQuery
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toCharacterRole
import com.example.shikiflow.data.mapper.shikimori.ShikimoriCharacterMapper.toDomain
import com.example.shikiflow.data.remote.CharacterApi
import com.example.shikiflow.domain.model.browse.Browse
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ShikimoriCharactersDataSource @Inject constructor(
    private val characterApi: CharacterApi,
    private val apolloClient: ApolloClient
): CharactersDataSource {
    override suspend fun getCharacterDetails(
        characterId: Int
    ): Flow<DataResult<MediaCharacter>> = flow {
        emit(DataResult.Loading)

        try{
            val response = characterApi.getCharacterDetails(characterId.toString()).toDomain()

            emit(DataResult.Success(response))
        } catch (e: Exception) {
            emit(DataResult.Error(e.message ?: "Unknown Error"))
        }
    }

    override fun getCharacterMediaRoles(
        characterId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Flow<PagingData<MediaRole>> {
        return Pager(config = PagingConfig(pageSize = Int.MAX_VALUE)) {
            object : PagingSource<Int, MediaRole>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MediaRole> {
                    return try {
                        val details = characterApi.getCharacterDetails(characterId.toString()).toDomain()

                        val mediaRoles = when(mediaType) {
                            MediaType.ANIME -> details.animeRoles.entries
                            MediaType.MANGA -> details.mangaRoles.entries
                        }.map { it.toCharacterRole() }

                        LoadResult.Page(
                            data = mediaRoles,
                            prevKey = null,
                            nextKey = null
                        )
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }

                override fun getRefreshKey(state: PagingState<Int, MediaRole>): Int? = null
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

    override suspend fun searchCharacters(
        page: Int,
        limit: Int,
        search: String
    ): Result<List<Browse.Character>> {
        if(search.isBlank()) {
            return Result.success(emptyList())
        }

        val searchQuery = CharacterSearchQuery(page, limit, search)
        val response = apolloClient.query(searchQuery).execute()

        return response.toResult().map { data ->
            data.characters
                .map { character ->
                    Browse.Character(
                        data = character.characterShort.toDomain()
                    )
                }
        }
    }
}