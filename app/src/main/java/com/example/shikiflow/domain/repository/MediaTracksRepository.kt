package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.media.MediaShortData
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.domain.model.track.media.MediaUserTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.ShortUserMediaRate
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface MediaTracksRepository {
    fun getLocalTrack(id: Int, mediaType: MediaType): Flow<MediaTrack?>

    suspend fun syncTracks()

    fun getMediaTracks(
        status: UserRateStatus,
        userId: Int?,
        mediaType: MediaType
    ): Flow<PagingData<MediaTrack>>

    fun browseMediaTracks(
        title: String,
        mediaType: MediaType,
        userRateStatus: UserRateStatus?,
        sort: Sort<UserRateType>,
        genres: List<Genre> = emptyList()
    ): Flow<PagingData<MediaTrack>>

    suspend fun getShortUserMediaRates(
        mediaType: MediaType
    ): List<ShortUserMediaRate>

    fun saveUserRate(
        entryId: Int? = null,
        mediaType: MediaType,
        mediaId: Int,
        malId: Int?,
        status: UserRateStatus,
        progress: Int? = null,
        progressVolumes: Int? = null,
        repeat: Int? = null,
        score: Int? = null,
        mediaShortData: MediaShortData? = null
    ): Flow<DataResult<UserMediaRate>>

    suspend fun updateMediaTrack(
        mediaTrack: MediaUserTrack,
        mediaShortData: MediaShortData? = null
    )

    fun deleteUserRate(
        entryId: Int,
        mediaId: Int,
        malId: Int?,
        mediaType: MediaType
    ): Flow<DataResult<Boolean>>
}