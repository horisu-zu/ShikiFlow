package com.example.shikiflow.data.datasource

import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.track.media.MediaTrack
import com.example.shikiflow.domain.model.tracks.MediaType

interface MediaTracksDataSource {
    suspend fun getMediaTracks(
        page: Int = 1,
        limit: Int = 50,
        mediaType: MediaType,
        userId: Int? = null,
        status: UserRateStatus? = null,
        order: Sort<UserRateType>? = null,
        idsList: List<Int>? = null
    ): Result<List<MediaTrack>>

    suspend fun browseMediaTracks(
        page: Int,
        limit: Int,
        mediaType: MediaType,
        userId: Int,
        title: String,
        userRateStatus: UserRateStatus? = null
    ): Result<List<MediaTrack>>
}