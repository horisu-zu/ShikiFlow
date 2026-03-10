package com.example.shikiflow.domain.model.tracks

import com.example.shikiflow.domain.model.media_details.MediaDetails
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.track.UserRateStatus
import kotlin.time.Clock
import kotlin.time.Instant

enum class MediaType {
    ANIME,
    MANGA;
}

data class UserRateData(
    val id: Int? = null,
    val mediaType: MediaType,
    val status: UserRateStatus,
    val progress: Int,
    val progressVolumes: Int,
    val rewatches: Int,
    val score: Int,
    val mediaId: Int,
    val title: String,
    val posterUrl: String?,
    val createDate: Instant,
    val updateDate: Instant,
    val totalCount: Int,
    val volumesCount: Int
) {
    companion object {
        fun MediaDetails.toUiModel(): UserRateData {
            return userRate?.let { userRate ->
                UserRateData(
                    id = userRate.rateId,
                    mediaType = mediaType,
                    status = userRate.rateStatus,
                    progress = userRate.progress,
                    progressVolumes = userRate.progressVolumes,
                    rewatches = userRate.repeat,
                    score = userRate.score,
                    mediaId = id,
                    title = title,
                    posterUrl = coverImageUrl,
                    createDate = userRate.createdAt,
                    updateDate = userRate.updatedAt,
                    totalCount = (if(status == MediaStatus.RELEASED) totalCount else currentProgress) ?: Int.MAX_VALUE,
                    volumesCount = if(status == MediaStatus.RELEASED) volumes ?: 0 else Int.MAX_VALUE
                )
            } ?: createEmpty(
                mediaId = id,
                mediaTitle = title,
                mediaPosterUrl = coverImageUrl,
                mediaType = mediaType,
                totalCount = (if(status == MediaStatus.RELEASED) totalCount else currentProgress) ?: Int.MAX_VALUE,
                volumesCount = if(status == MediaStatus.RELEASED) volumes ?: 0 else Int.MAX_VALUE
            )
        }

        fun createEmpty(
            mediaId: Int,
            mediaTitle: String,
            mediaPosterUrl: String,
            totalCount: Int,
            volumesCount: Int,
            mediaType: MediaType
        ) = UserRateData(
            mediaType = mediaType,
            status = UserRateStatus.UNKNOWN,
            totalCount = totalCount,
            volumesCount = volumesCount,
            progress = 0,
            progressVolumes = 0,
            rewatches = 0,
            score = 0,
            mediaId = mediaId,
            title = mediaTitle,
            posterUrl = mediaPosterUrl,
            createDate = Clock.System.now(),
            updateDate = Clock.System.now()
        )
    }
}

data class SaveUserRate(
    val rateId: Int? = null,
    val mediaId: Int,
    val userStatus: UserRateStatus,
    val score: Int = 0,
    val progress: Int = 0,
    val progressVolumes: Int? = null,
    val repeat: Int = 0
)
