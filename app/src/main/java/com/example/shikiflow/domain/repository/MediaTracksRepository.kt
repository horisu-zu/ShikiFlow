package com.example.shikiflow.domain.repository

import androidx.paging.PagingData
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.media.MediaShortData
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.domain.model.track.media.MediaUserTrack
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow

interface MediaTracksRepository {
    fun getMediaTracks(
        status: UserRateStatus,
        userId: Int?,
        mediaType: MediaType
    ): Flow<PagingData<MediaTrack>>

    fun browseMediaTracks(
        userId: Int,
        mediaType: MediaType,
        title: String,
        userRateStatus: UserRateStatus? = null
    ): Flow<PagingData<MediaTrack>>

    fun saveUserRate(
        userId: Int? = null,
        entryId: Int? = null,
        mediaType: MediaType,
        mediaId: Int,
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
}