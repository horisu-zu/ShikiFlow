package com.example.shikiflow.domain.model.media_details

import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.track.Date
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.tracks.UserMediaRate
import com.example.shikiflow.domain.model.user.Stat
import kotlin.time.Instant

data class MediaDetails(
    val id: Int,
    val malId: Int,
    val mediaType: MediaType,
    val title: String,
    val descriptionHtml: String?,
    val native: String?,
    val synonyms: List<String>,
    val coverImageUrl: String,
    val score: Float?,
    val totalCount: Int?,
    val currentProgress: Int? = null,
    val volumes: Int? = null,
    val format: MediaFormat,
    val status: MediaStatus,
    val ageRating: AgeRating? = null,
    val genres: List<String>,
    val characters: PaginatedList<MediaPersonShort>,
    val airedOn: Date?,
    val releasedOn: Date?,
    val nextEpisodeAt: Instant? = null,
    val origin: MediaOrigin? = null,
    val screenshots: List<String> = emptyList(),
    val userRate: UserMediaRate?,
    val studios: List<Studio>? = null,
    val staffList: List<StaffShort> = emptyList(),
    val durationMins: Int? = null,
    val relatedMedia: List<RelatedMedia>,
    val scoreStats: List<Stat<Int>>,
    val statusesStats: List<Stat<UserRateStatus>>,
    val threadId: Int? = null
)
