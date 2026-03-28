package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import com.example.graphql.anilist.CharacterDetailsQuery
import com.example.graphql.anilist.CharacterMediaAppearancesQuery
import com.example.graphql.anilist.CharactersQuery
import com.example.shikiflow.data.datasource.CharactersDataSource
import com.example.shikiflow.data.local.source.CharacterMediaPagingSource
import com.example.shikiflow.data.mapper.anilist.AnilistCharacterMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistCharacterMapper.toCharacterMediaRole
import com.example.shikiflow.data.mapper.common.MediaTypeMapper.toAnilistType
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistMediaSort
import com.example.shikiflow.domain.model.character.MediaCharacterShort
import com.example.shikiflow.domain.model.character.MediaCharacter
import com.example.shikiflow.domain.model.common.CharacterMediaRole
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnilistCharactersDataSource @Inject constructor(
    private val apolloClient: ApolloClient
): CharactersDataSource, BaseNetworkRepository() {
    override suspend fun getCharacterDetails(characterId: Int): Flow<DataResult<MediaCharacter>> {
        val characterQuery = CharacterDetailsQuery(characterId)

        val response = apolloClient.query(characterQuery)
            .toFlow()
            .asDataResult { data ->
                data.Character?.toDomain() ?: throw NoSuchElementException("Character Not Found")
            }

        return response
    }

    override fun getCharacterMediaRoles(
        characterId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Flow<PagingData<MediaRole>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                CharacterMediaPagingSource(
                    characterId = characterId,
                    mediaType = mediaType,
                    sort = sort,
                    charactersDataSource = this
                )
            }
        ).flow
    }

    suspend fun paginatedCharacterMediaAppearances(
        page: Int,
        limit: Int,
        characterId: Int,
        mediaType: MediaType,
        sort: Sort<MediaSort>
    ): Result<List<CharacterMediaRole>> {
        val characterMediaAppearanceQuery = CharacterMediaAppearancesQuery(
            page = page,
            perPage = limit,
            characterId = characterId,
            mediaType = mediaType.toAnilistType(),
            sort = sort.toAnilistMediaSort()
        )

        val response = apolloClient.query(characterMediaAppearanceQuery).execute()

        return response.toResult().map { data ->
            data.Character
                ?.media
                ?.aLCharacterMediaRoles
                ?.toCharacterMediaRole() ?: emptyList()
        }
    }

    override suspend fun loadMediaCharacters(
        page: Int,
        limit: Int,
        mediaId: Int,
        mediaType: MediaType
    ): Result<List<MediaCharacterShort>> {
        val charactersQuery = CharactersQuery(mediaId, page, limit)

        val response = apolloClient.query(charactersQuery).execute()

        if(response.hasErrors()) {
            return Result.failure(Exception("Response Errors: ${response.errors}"))
        }

        return response.toResult().map { data ->
            data.Media
                ?.characters
                ?.edges
                ?.mapNotNull { characterEdge ->
                    characterEdge?.toDomain()
                } ?: emptyList()
        }
    }
}