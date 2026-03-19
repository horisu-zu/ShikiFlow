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
import com.example.shikiflow.data.datasource.MediaDetailsDataSource
import com.example.shikiflow.data.local.source.BrowsePagingSource
import com.example.shikiflow.data.mapper.common.ExternalLinksMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toShikiAnimeKind
import com.example.shikiflow.data.mapper.common.MediaFormatMapper.toShikiMangaKind
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toShikimoriAnimeStatus
import com.example.shikiflow.data.mapper.common.MediaStatusMapper.toShikimoriMangaStatus
import com.example.shikiflow.data.mapper.common.OrderMapper.toShikimoriBrowseOrder
import com.example.shikiflow.data.mapper.common.RatingMapper.toShikiRating
import com.example.shikiflow.data.mapper.common.SeasonMapper.toShikiSeason
import com.example.shikiflow.data.mapper.shikimori.ShikimoriDetailsMapper.toBrowseAnime
import com.example.shikiflow.data.mapper.shikimori.ShikimoriDetailsMapper.toBrowseManga
import com.example.shikiflow.data.mapper.shikimori.ShikimoriDetailsMapper.toDomain
import com.example.shikiflow.data.remote.AnimeApi
import com.example.shikiflow.data.remote.MangaApi
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.BrowseType
import com.example.shikiflow.domain.model.media_details.ExternalLinkData
import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.utils.AnilistUtils.toResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShikimoriMediaDetailsDataSource @Inject constructor(
    private val apolloClient: ApolloClient,
    private val animeApi: AnimeApi,
    private val mangaApi: MangaApi
): MediaDetailsDataSource {

    override suspend fun getMediaDetails(
        id: Int,
        mediaType: MediaType
    ): Result<MediaDetails> {
        return when (mediaType) {
            MediaType.ANIME -> {
                val response = apolloClient
                    .query(
                        AnimeDetailsQuery(
                            ids = Optional.presentIfNotNull(id.toString())
                        )
                    )
                    .fetchPolicy(FetchPolicy.NetworkFirst)
                    .execute()

                response.toResult().map { data ->
                    data.animes.firstOrNull()?.toDomain()
                        ?: throw IllegalStateException("No Anime Details data returned")
                }
            }

            MediaType.MANGA -> {
                val response = apolloClient
                    .query(
                        MangaDetailsQuery(
                            ids = Optional.presentIfNotNull(id.toString())
                        )
                    )
                    .fetchPolicy(FetchPolicy.NetworkFirst)
                    .execute()

                response.toResult().map { data ->
                    data.mangas.firstOrNull()?.toDomain()
                        ?: throw IllegalStateException("No Anime Details data returned")
                }
            }
        }
    }

    override fun paginatedBrowseMedia(
        browseType: BrowseType?,
        browseOptions: BrowseOptions
    ): Flow<PagingData<Browse>> {
        return Pager(
            config = PagingConfig(
                pageSize = 24,
                enablePlaceholders = true,
                prefetchDistance = 12,
                initialLoadSize = 24
            ),
            pagingSourceFactory = {
                BrowsePagingSource(
                    mediaDetailsDataSource = this,
                    browseType = browseType,
                    options = browseOptions
                )
            }
        ).flow
    }

    override suspend fun browseMedia(
        page: Int,
        limit: Int,
        browseOptions: BrowseOptions
    ): Result<List<Browse>> {
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
                    data.mangas.map { anime ->
                        anime.toBrowseManga()
                    }
                }
            }
        }
    }

    override fun getSimilarMedia(
        mediaType: MediaType,
        mediaId: Int
    ): Flow<PagingData<Browse>> {
        return Pager(config = PagingConfig(pageSize = 100)) {
            object : PagingSource<Int, Browse>() {
                override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Browse> {
                    val response = when(mediaType) {
                        MediaType.ANIME -> animeApi.getSimilarAnime(mediaId.toString()).map { it.toBrowseAnime() }
                        MediaType.MANGA -> mangaApi.getSimilarManga(mediaId.toString()).map { it.toBrowseManga() }
                    }

                    return LoadResult.Page(
                        data = response,
                        prevKey = null,
                        nextKey = null
                    )
                }
                override fun getRefreshKey(state: PagingState<Int, Browse>): Int? = null
            }
        }.flow
    }

    override suspend fun loadMediaRecommendations(
        mediaType: MediaType,
        mediaId: Int,
        page: Int,
        limit: Int
    ): Result<List<Browse>> {
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
        order: SortType,
        onList: Boolean?
    ): Result<List<Browse>> {
        val query = AnimeBrowseQuery(
            page = Optional.presentIfNotNull(page),
            limit = Optional.presentIfNotNull(limit),
            search = Optional.presentIfNotNull(search),
            order = Optional.presentIfNotNull(order.toShikimoriBrowseOrder()),
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