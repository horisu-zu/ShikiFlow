package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.MediaStatus as AnilistMediaStatus
import com.example.graphql.shikimori.type.AnimeStatusEnum
import com.example.graphql.shikimori.type.MangaStatusEnum
import com.example.shikiflow.domain.model.media_details.MediaStatus

object MediaStatusMapper {
    fun AnimeStatusEnum.toDomain(): MediaStatus {
        return when(this) {
            AnimeStatusEnum.anons -> MediaStatus.ANNOUNCED
            AnimeStatusEnum.ongoing -> MediaStatus.ONGOING
            AnimeStatusEnum.released -> MediaStatus.RELEASED
            AnimeStatusEnum.UNKNOWN__ -> MediaStatus.UNKNOWN
        }
    }

    fun MangaStatusEnum.toDomain(): MediaStatus {
        return when(this) {
            MangaStatusEnum.anons -> MediaStatus.ANNOUNCED
            MangaStatusEnum.ongoing -> MediaStatus.ONGOING
            MangaStatusEnum.released -> MediaStatus.RELEASED
            MangaStatusEnum.paused -> MediaStatus.HIATUS
            MangaStatusEnum.discontinued -> MediaStatus.CANCELLED
            MangaStatusEnum.UNKNOWN__ -> MediaStatus.UNKNOWN
        }
    }

    fun MediaStatus.toShikimoriAnimeStatus(): AnimeStatusEnum {
        return when(this) {
            MediaStatus.ANNOUNCED -> AnimeStatusEnum.anons
            MediaStatus.ONGOING -> AnimeStatusEnum.ongoing
            MediaStatus.RELEASED -> AnimeStatusEnum.released
            else -> AnimeStatusEnum.UNKNOWN__
        }
    }

    fun MediaStatus.toShikimoriMangaStatus(): MangaStatusEnum {
        return when(this) {
            MediaStatus.ANNOUNCED -> MangaStatusEnum.anons
            MediaStatus.ONGOING -> MangaStatusEnum.ongoing
            MediaStatus.RELEASED -> MangaStatusEnum.released
            MediaStatus.CANCELLED -> MangaStatusEnum.discontinued
            MediaStatus.HIATUS -> MangaStatusEnum.paused
            MediaStatus.UNKNOWN -> MangaStatusEnum.UNKNOWN__
        }
    }

    fun AnilistMediaStatus.toDomain(): MediaStatus {
        return when(this) {
            AnilistMediaStatus.FINISHED -> MediaStatus.RELEASED
            AnilistMediaStatus.RELEASING -> MediaStatus.ONGOING
            AnilistMediaStatus.NOT_YET_RELEASED -> MediaStatus.ANNOUNCED
            AnilistMediaStatus.CANCELLED -> MediaStatus.CANCELLED
            AnilistMediaStatus.HIATUS -> MediaStatus.HIATUS
            AnilistMediaStatus.UNKNOWN__ -> MediaStatus.UNKNOWN
        }
    }

    fun MediaStatus.toAnilistStatus(): AnilistMediaStatus {
        return when(this) {
            MediaStatus.ANNOUNCED -> AnilistMediaStatus.NOT_YET_RELEASED
            MediaStatus.ONGOING -> AnilistMediaStatus.RELEASING
            MediaStatus.RELEASED -> AnilistMediaStatus.FINISHED
            MediaStatus.CANCELLED ->  AnilistMediaStatus.CANCELLED
            MediaStatus.HIATUS -> AnilistMediaStatus.HIATUS
            MediaStatus.UNKNOWN -> AnilistMediaStatus.UNKNOWN__
        }
    }
}