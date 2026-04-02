package com.example.shikiflow.data.datasource.shikimori

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.example.graphql.shikimori.AnimeBrowseQuery
import com.example.graphql.shikimori.AnimeDetailsQuery
import com.example.graphql.shikimori.MangaBrowseQuery
import com.example.graphql.shikimori.MangaDetailsQuery
import com.example.graphql.shikimori.type.AnimeStatusEnum
import com.example.graphql.shikimori.type.OrderEnum
import com.example.shikiflow.data.datasource.MediaDataSource
import com.example.shikiflow.data.local.source.AiringPagingSource
import com.example.shikiflow.data.local.source.BrowsePagingSource
import com.example.shikiflow.data.mapper.common.ExternalLinksMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toShikiAnimeKind
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toShikiMangaKind
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toShikimoriAnimeStatus
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toShikimoriMangaStatus
import com.example.shikiflow.data.mapper.common.OrderMapper.toShikimoriBrowseOrder
import com.example.shikiflow.data.mapper.common.RatingMapper.toShikiRating
import com.example.shikiflow.data.mapper.common.SeasonMapper
import com.example.shikiflow.data.mapper.common.SeasonMapper.toShikiSeason
import com.example.shikiflow.data.mapper.shikimori.ShikimoriMediaMapper.toAiringAnime
import com.example.shikiflow.data.mapper.shikimori.ShikimoriMediaMapper.toBrowseAnime
import com.example.shikiflow.data.mapper.shikimori.ShikimoriMediaMapper.toBrowseManga
import com.example.shikiflow.data.mapper.shikimori.ShikimoriMediaMapper.toDomain
import com.example.shikiflow.data.remote.AnimeApi
import com.example.shikiflow.data.remote.MangaApi
import com.example.shikiflow.domain.model.anime.AiringAnime
import com.example.shikiflow.domain.model.browse.BrowseMedia
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShikimoriMediaDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val animeApi: AnimeApi,
    private val mangaApi: MangaApi
): MediaDataSource, BaseNetworkRepository() {

    override fun getMediaDetails(
        id: Int,
        mediaType: MediaType
    ): Flow<DataResult<MediaDetails>> {
        return when (mediaType) {
            MediaType.ANIME -> {
                apolloClient
                    .query(
                        AnimeDetailsQuery(
                            ids = Optional.presentIfNotNull(id.toString())
                        )
                    )
                    .fetchPolicy(FetchPolicy.NetworkFirst)
                    .toFlow()
                    .asDataResult { data ->
                        data.animes.firstOrNull()?.toDomain()
                            ?: throw IllegalStateException("No Anime Details data returned")
                    }
            }

            MediaType.MANGA -> {
                apolloClient
                    .query(
                        MangaDetailsQuery(
                            ids = Optional.presentIfNotNull(id.toString())
                        )
                    )
                    .fetchPolicy(FetchPolicy.NetworkFirst)
                    .toFlow()
                    .asDataResult { data ->
                        data.mangas.firstOrNull()?.toDomain()
                            ?: throw IllegalStateException("No Anime Details data returned")
                    }
            }
        }
    }

    override fun paginatedBrowseMedia(
        browseOptions: MediaBrowseOptions
    ): Flow<PagingData<BrowseMedia>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                BrowsePagingSource(
                    mediaDataSource = this,
                    options = browseOptions
                )
            }
        ).flow
    }

    override suspend fun browseMedia(
        page: Int,
        limit: Int,
        browseOptions: MediaBrowseOptions
    ): Result<List<BrowseMedia>> {
        return  when(browseOptions.mediaType) {
            MediaType.ANIME -> {
                val query = AnimeBrowseQuery(
                    page = Optional.presentIfNotNull(page),
                    limit = Optional.presentIfNotNull(limit),
                    search = Optional.presentIfNotNull(browseOptions.name),
                    order = Optional.presentIfNotNull(browseOptions.order?.toShikimoriBrowseOrder()),
                    kind = Optional.presentIfNotNull(browseOptions.format?.toShikiAnimeKind()?.name),
                    status = Optional.presentIfNotNull(browseOptions.status?.toShikimoriAnimeStatus()?.name),
                    season = Optional.presentIfNotNull(browseOptions.season?.toShikiSeason()),
                    score = Optional.presentIfNotNull(browseOptions.score),
                    genre = Optional.presentIfNotNull(browseOptions.genre),
                    rating = Optional.presentIfNotNull(browseOptions.ageRating?.toShikiRating()?.name)
                )

                val response = apolloClient.query(query).execute()

                Log.d("ShikimoriMediaDataSource", "Query: $query")
                Log.d("ShikimoriMediaDataSource", "Response: $response")

                response.toResult().map { data ->
                    data.animes.map { anime ->
                        anime.toBrowseAnime()
                    }
                }
            }
            MediaType.MANGA -> {
                val query = MangaBrowseQuery(
                    page = Optional.presentIfNotNull(page),
                    limit = Optional.presentIfNotNull(limit),
                    search = Optional.presentIfNotNull(browseOptions.name),
                    order = Optional.presentIfNotNull(browseOptions.order?.toShikimoriBrowseOrder()),
                    kind = Optional.presentIfNotNull(browseOptions.format?.toShikiMangaKind()?.name),
                    status = Optional.presentIfNotNull(browseOptions.status?.toShikimoriMangaStatus()?.name),
                    genre = Optional.presentIfNotNull(browseOptions.genre),
                    score = Optional.presentIfNotNull(browseOptions.score),
                )

                val response = apolloClient.query(query).execute()

                response.toResult().map { data ->
                    data.mangas.map { manga ->
                        manga.toBrowseManga()
                    }
                }
            }
        }
    }

    override fun getAiringAnimes(
        onList: Boolean,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Flow<PagingData<AiringAnime>> {
        val seenIds = mutableSetOf<Int>()

        return Pager(
            config = PagingConfig(
                pageSize = 45,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 45
            ),
            pagingSourceFactory = {
                seenIds.clear()
                AiringPagingSource(
                    mediaDataSource = this,
                    onList = onList,
                    airingAtGreater = airingAtGreater,
                    airingAtLesser = airingAtLesser
                )
            }
        ).flow
    }

    override suspend fun getAiringSchedule(
        page: Int,
        limit: Int,
        airingAtGreater: Long,
        airingAtLesser: Long
    ): Result<List<AiringAnime>> = runCatching {
        val ongoingsQuery = AnimeBrowseQuery(
            page = Optional.present(page),
            limit = Optional.present(limit),
            status = Optional.present(AnimeStatusEnum.ongoing.name),
            order = Optional.present(OrderEnum.ranked),
            score = Optional.present(1)
        )

        //Covers released on the same week titles within a season
        val seasonQuery = AnimeBrowseQuery(
            page = Optional.present(page),
            limit = Optional.present(limit),
            order = Optional.present(OrderEnum.ranked),
            season = Optional.present(SeasonMapper.currentShikiSeason()),
            score = Optional.present(1)
        )

        val ongoingsResult = apolloClient.query(ongoingsQuery).execute()
            .toResult()
            .map { data ->
                Log.d("ShikimoriMediaDataSource", "Ongoings Size: ${data.animes.size}")
                data.animes.map { anime ->
                    anime.toAiringAnime()
                }
            }.getOrThrow()

        val seasonResult = apolloClient.query(seasonQuery).execute()
            .toResult()
            .map { data ->
                Log.d("ShikimoriMediaDataSource", "Season Size: ${data.animes.size}")
                data.animes.map { anime ->
                    anime.toAiringAnime()
                }
            }.getOrThrow()

        (ongoingsResult + seasonResult).distinctBy { it.data.id }
    }

    override fun getSimilarMedia(
        mediaType: MediaType,
        mediaId: Int
    ): Flow<PagingData<BrowseMedia>> {
        return Pager(config = PagingConfig(pageSize = Int.MAX_VALUE)) {
            object : PagingSource<Int, BrowseMedia>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BrowseMedia> {
                    return try {
                        val response = when(mediaType) {
                            MediaType.ANIME -> animeApi.getSimilarAnime(mediaId.toString()).map { it.toBrowseAnime() }
                            MediaType.MANGA -> mangaApi.getSimilarManga(mediaId.toString()).map { it.toBrowseManga() }
                        }

                        return LoadResult.Page(
                            data = response,
                            prevKey = null,
                            nextKey = null
                        )
                    } catch (e: Exception) {
                        LoadResult.Error(e)
                    }
                }
                override fun getRefreshKey(state: PagingState<Int, BrowseMedia>): Int? = null
            }
        }.flow
    }

    override suspend fun loadMediaRecommendations(
        mediaType: MediaType,
        mediaId: Int,
        page: Int,
        limit: Int
    ): Result<List<BrowseMedia>> {
        return try {
            val response = when(mediaType) {
                MediaType.ANIME -> animeApi.getSimilarAnime(mediaId.toString()).map { it.toBrowseAnime() }
                MediaType.MANGA -> mangaApi.getSimilarManga(mediaId.toString()).map { it.toBrowseManga() }
            }

            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getExternalLinks(
        mediaType: MediaType,
        mediaId: Int
    ): Result<List<ExternalLinkData>> {
        return try {
            val result = when(mediaType) {
                MediaType.ANIME -> animeApi.getExternalLinks(mediaId.toString())
                MediaType.MANGA -> mangaApi.getExternalLinks(mediaId.toString())
            }.map { result -> result.toDomain() }

            Result.success(result)
        } catch (e: Exception) {
            Result.failure(exception = e)
        }
    }

    override suspend fun loadStudioMedia(
        studioId: Int,
        page: Int,
        limit: Int,
        search: String?,
        order: SortType?,
        onList: Boolean?
    ): Result<List<BrowseMedia>> {
        val query = AnimeBrowseQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            search = Optional.presentIfNotNull(search),
            order = Optional.presentIfNotNull(order?.toShikimoriBrowseOrder()),
            mylist = Optional.presentIfNotNull(onList),
            studio = Optional.presentIfNotNull(studioId.toString())
        )

        val response = apolloClient.query(query).execute()


        return response.toResult().map { data ->
            data.animes.map { anime ->
                anime.toBrowseAnime()
            }
        }
    }
}