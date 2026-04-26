package com.example.shikiflow.data.datasource.shikimori

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.cache.normalized.FetchPolicy
import com.apollographql.apollo.cache.normalized.fetchPolicy
import com.example.graphql.shikimori.AnimeBrowseQuery
import com.example.graphql.shikimori.AnimeTrackBrowseQuery
import com.example.graphql.shikimori.AnimeTracksQuery
import com.example.graphql.shikimori.MangaBrowseQuery
import com.example.graphql.shikimori.MangaTracksBrowseQuery
import com.example.graphql.shikimori.MangaTracksQuery
import com.example.shikiflow.data.datasource.MediaTracksDataSource
import com.example.shikiflow.data.datasource.dto.ShikiCreateRateRequest
import com.example.shikiflow.data.datasource.dto.ShikiUpdateRateRequest
import com.example.shikiflow.data.mapper.common.OrderMapper.toShikimoriOrder
import com.example.shikiflow.data.mapper.common.RateStatusMapper.toShikimoriRateStatus
import com.example.shikiflow.data.mapper.shikimori.ShikimoriRateMapper.toDomain
import com.example.shikiflow.data.remote.UserApi
import com.example.shikiflow.di.annotations.ShikimoriApollo
import com.example.shikiflow.data.mapper.local.TracksMapper.toDomain as toMediaDomain
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.utils.AnilistUtils.toResult
import com.example.shikiflow.utils.DataResult
import javax.inject.Inject
import kotlin.collections.map

@OptIn(ExperimentalPagingApi::class)
class ShikimoriTracksDataSource @Inject constructor(
    @param:ShikimoriApollo private val apolloClient: ApolloClient,
    private val userApi: UserApi
): MediaTracksDataSource {
    override suspend fun getMediaTracks(
        page: Int,
        limit: Int,
        mediaType: MediaType,
        userId: Int?,
        status: UserRateStatus?,
        order: Sort<UserRateType>?,
        idsList: List<Int>?
    ): Result<List<MediaTrack>> {
        when(mediaType) {
            MediaType.ANIME -> {
                val query = AnimeTracksQuery(
                    page = Optional.presentIfNotNull(page),
                    limit = Optional.presentIfNotNull(limit),
                    userId = Optional.presentIfNotNull(userId.toString()),
                    status = Optional.presentIfNotNull(status?.toShikimoriRateStatus()),
                    order = Optional.presentIfNotNull(order?.toShikimoriOrder())
                )

                val response = apolloClient.query(query)
                    .fetchPolicy(FetchPolicy.NetworkFirst)
                    .execute()

                return response.toResult().map { data ->
                    data.userRates.map { userRate ->
                        userRate.animeUserRateWithModel.toMediaDomain()
                    }
                }
            }
            MediaType.MANGA -> {
                val query = MangaTracksQuery(
                    page = Optional.presentIfNotNull(page),
                    limit = Optional.presentIfNotNull(limit),
                    userId = Optional.presentIfNotNull(userId.toString()),
                    status = Optional.presentIfNotNull(status?.toShikimoriRateStatus()),
                    order = Optional.presentIfNotNull(order?.toShikimoriOrder())
                )

                val response = apolloClient.query(query)
                    .fetchPolicy(FetchPolicy.NetworkFirst)
                    .execute()

                return response.toResult().map { data ->
                    data.userRates.map { userRate ->
                        userRate.mangaUserRateWithModel.toMediaDomain()
                    }
                }
            }
        }
    }

    override suspend fun browseMediaTracks(
        page: Int,
        limit: Int,
        mediaType: MediaType,
        userId: Int,
        title: String,
        userRateStatus: UserRateStatus?
    ): Result<List<MediaTrack>> {
        if(title.isBlank()) {
            return Result.success(emptyList())
        }

        when(mediaType) {
            MediaType.ANIME -> {
                val query = AnimeTrackBrowseQuery(
                    page = Optional.presentIfNotNull(page),
                    limit = Optional.presentIfNotNull(limit),
                    search = Optional.present(title),
                    mylist = when (userRateStatus) {
                        null -> Optional.present(
                            value = UserRateStatus.entries
                                .filter { it != UserRateStatus.UNKNOWN }
                                .joinToString(",") { it.toShikimoriRateStatus().name }
                        )
                        else -> Optional.present(userRateStatus.toShikimoriRateStatus().name)
                    }
                )
                Log.d("ShikimoriTracksDataSource", "Query: $query")

                val response = apolloClient.query(query).execute()

                return response.toResult().map { data ->
                    data.animes.mapNotNull { anime ->
                        anime.userRate?.animeUserRateWithModel?.toMediaDomain()
                    }
                }
            }
            MediaType.MANGA -> {
                val query = MangaTracksBrowseQuery(
                    page = Optional.presentIfNotNull(page),
                    limit = Optional.presentIfNotNull(limit),
                    search = Optional.present(title),
                    mylist = when (userRateStatus) {
                        null -> Optional.present(
                            value = UserRateStatus.entries
                                .filter { it != UserRateStatus.UNKNOWN }
                                .joinToString(",") { it.toShikimoriRateStatus().name }
                        )
                        else -> Optional.present(userRateStatus.toShikimoriRateStatus().name)
                    }
                )
                Log.d("ShikimoriTracksDataSource", "Query: $query")

                val response = apolloClient.query(query).execute()

                return response.toResult().map { data ->
                    data.mangas.mapNotNull { anime ->
                        anime.userRate?.mangaUserRateWithModel?.toMediaDomain()
                    }
                }
            }
        }
    }

    override suspend fun saveUserRate(
        userId: Int?,
        entryId: Int?,
        mediaType: MediaType,
        mediaId: Int,
        status: UserRateStatus,
        progress: Int?,
        progressVolumes: Int?,
        repeat: Int?,
        score: Int?
    ): UserMediaRate {
        return when(entryId) {
            null -> {
                val createRequest = ShikiCreateRateRequest(
                    userId = userId?.toLong()!!,
                    targetId = mediaId.toLong(),
                    status = status.toShikimoriRateStatus().name,
                    targetType = mediaType.name.lowercase().replaceFirstChar { it.uppercase() },
                    episodes = if(mediaType == MediaType.ANIME) progress else null,
                    chapters = if(mediaType == MediaType.MANGA) progress else null,
                    volumes = progressVolumes,
                    rewatches = repeat,
                    score = score
                )

                Log.d("ShikimoriTracksDataSource", "Create Request: $createRequest")

                userApi.createUserRate(createRequest = createRequest)
            }
            else -> {
                val updateRequest = ShikiUpdateRateRequest(
                    chapters = if(mediaType == MediaType.MANGA) progress else null,
                    episodes = if(mediaType == MediaType.ANIME) progress else null,
                    volumes = progressVolumes,
                    rewatches = repeat,
                    score = score,
                    status = status.toShikimoriRateStatus().name
                )

                Log.d("ShikimoriTracksDataSource", "Update Request: $updateRequest")

                userApi.updateUserRate(
                    id = entryId.toLong(),
                    request = updateRequest
                )
            }
        }.toDomain(mediaType)
    }

    override suspend fun saveServiceUserRate(
        userId: Int,
        mediaType: MediaType,
        malId: Int,
        status: UserRateStatus,
        progress: Int?,
        progressVolumes: Int?,
        repeat: Int?,
        score: Int?
    ) {
        val entryId = getServiceUserRate(malId, mediaType)

        saveUserRate(
            userId = userId,
            entryId = entryId,
            mediaType = mediaType,
            mediaId = malId,
            status = status,
            progress = progress,
            progressVolumes = progressVolumes,
            repeat = repeat,
            score = score
        )
    }

    override suspend fun deleteUserRate(entryId: Int): DataResult<Boolean> {
        return try {
            userApi.deleteUserRate(entryId.toLong())

            DataResult.Success(true)
        } catch (e: Exception) {
            DataResult.Error(e.message ?: "Unknown Error")
        }
    }

    override suspend fun deleteServiceUserRate(
        userId: Int,
        malId: Int,
        mediaType: MediaType
    ) {
        val entryId = getServiceUserRate(malId, mediaType)

        entryId?.let {
            deleteUserRate(entryId)
        }
    }

    suspend fun getServiceUserRate(
        malId: Int,
        mediaType: MediaType
    ): Int? {
        return when(mediaType) {
            MediaType.ANIME -> {
                val query = AnimeBrowseQuery(
                    ids = Optional.present(malId.toString())
                )

                val response = apolloClient.query(query)
                    .fetchPolicy(FetchPolicy.NetworkOnly)
                    .execute()

                response.data
                    ?.animes
                    ?.firstOrNull()
                    ?.userRate?.animeUserRate?.id
            }
            MediaType.MANGA -> {
                val query = MangaBrowseQuery(
                    ids = Optional.present(malId.toString())
                )

                val response = apolloClient.query(query)
                    .fetchPolicy(FetchPolicy.NetworkOnly)
                    .execute()

                response.data
                    ?.mangas
                    ?.firstOrNull()
                    ?.userRate?.mangaUserRate?.id
            }
        }?.toIntOrNull()
    }
}