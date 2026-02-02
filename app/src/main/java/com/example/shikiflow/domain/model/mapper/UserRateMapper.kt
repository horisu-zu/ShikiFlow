package com.example.shikiflow.domain.model.mapper

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaOrigin
import com.example.shikiflow.domain.model.track.RelationKind
import com.example.shikiflow.domain.model.track.Date
import com.example.shikiflow.domain.model.tracks.MediaType

class UserRateMapper {
    companion object {
        fun mapUserRateStatus(status: UserRateStatus, mediaType: MediaType = MediaType.ANIME): Int {
            return when(status) {
                UserRateStatus.WATCHING -> if(mediaType == MediaType.ANIME) {
                   R.string.media_user_status_anime_watching
                } else { R.string.media_user_status_manga_reading }
                UserRateStatus.PLANNED -> R.string.media_user_status_planned
                UserRateStatus.COMPLETED -> R.string.media_user_status_completed
                UserRateStatus.REWATCHING -> if(mediaType == MediaType.ANIME) {
                    R.string.media_user_status_anime_rewatching
                } else { R.string.media_user_status_manga_rereading }
                UserRateStatus.PAUSED -> R.string.media_user_status_paused
                UserRateStatus.DROPPED -> R.string.media_user_status_dropped
                UserRateStatus.UNKNOWN -> R.string.media_user_status_unknown
            }
        }

        fun mapOriginToString(origin: MediaOrigin): Int {
            return when(origin) {
                MediaOrigin.ORIGINAl -> R.string.anime_origin_original
                MediaOrigin.MANGA -> R.string.anime_origin_manga
                MediaOrigin.WEB_MANGA -> R.string.anime_origin_web_manga
                MediaOrigin.FOUR_KOMA_MANGA -> R.string.anime_origin_4_koma_manga
                MediaOrigin.NOVEL -> R.string.anime_origin_novel
                MediaOrigin.WEB_NOVEL -> R.string.anime_origin_web_novel
                MediaOrigin.VISUAL_NOVEL -> R.string.anime_origin_visual_novel
                MediaOrigin.LIGHT_NOVEL -> R.string.anime_origin_light_novel
                MediaOrigin.GAME -> R.string.anime_origin_game
                MediaOrigin.CARD_GAME -> R.string.anime_origin_card_game
                MediaOrigin.MUSIC -> R.string.anime_origin_music
                MediaOrigin.RADIO -> R.string.anime_origin_radio
                MediaOrigin.BOOK -> R.string.anime_origin_book
                MediaOrigin.PICTURE_BOOK -> R.string.anime_origin_picture_book
                MediaOrigin.MIXED_MEDIA -> R.string.anime_origin_mixed_media
                MediaOrigin.OTHER -> R.string.anime_origin_other
                else -> R.string.common_unknown
            }
        }

        @Composable
        fun mapUserRateStatusToString(
            status: UserRateStatus,
            watchedEpisodes: Int?,
            allEpisodes: Int,
            score: Int? = null,
            mediaType: MediaType = MediaType.ANIME
        ): String {
            val progressSuffix = if (allEpisodes != 0) {
                stringResource(R.string.progress_suffix, watchedEpisodes ?: 0, allEpisodes)
            } else ""

            val scoreSuffix = if (score != null && score > 0) {
                stringResource(R.string.score_suffix, score.toString())
            } else ""

            return when (status) {
                UserRateStatus.WATCHING -> {
                    val resId = if (mediaType == MediaType.ANIME) R.string.media_user_status_anime_watching
                        else R.string.media_user_status_manga_reading
                    stringResource(resId) + progressSuffix
                }
                UserRateStatus.PLANNED -> stringResource(R.string.media_user_status_planned)
                UserRateStatus.COMPLETED -> {
                    stringResource(R.string.media_user_status_completed) + scoreSuffix
                }
                UserRateStatus.REWATCHING -> {
                    val resId = if (mediaType == MediaType.ANIME) R.string.media_user_status_anime_rewatching
                        else R.string.media_user_status_manga_rereading
                    stringResource(resId)
                }
                UserRateStatus.PAUSED -> {
                    stringResource(R.string.media_user_status_paused) + progressSuffix
                }
                UserRateStatus.DROPPED -> {
                    stringResource(R.string.media_user_status_dropped) + (scoreSuffix.takeIf { it.isNotEmpty() } ?: progressSuffix)
                }
                UserRateStatus.UNKNOWN -> stringResource(R.string.media_user_status_unknown)
            }
        }

        fun isWatched(status: UserRateStatus): Boolean {
            return setOf(
                UserRateStatus.WATCHING,
                UserRateStatus.DROPPED,
                UserRateStatus.PAUSED
            ).contains(status)
        }

        fun mapRelationKind(kind: RelationKind?): Int {
            return when(kind) {
                RelationKind.ADAPTATION -> R.string.relation_kind_adaptation
                RelationKind.ALTERNATIVE_SETTING -> R.string.relation_kind_alternative_setting
                RelationKind.ALTERNATIVE_VERSION -> R.string.relation_kind_alternative_version
                RelationKind.CHARACTER -> R.string.relation_kind_character
                RelationKind.FULL_STORY -> R.string.relation_kind_full_story
                RelationKind.OTHER -> R.string.relation_kind_other
                RelationKind.PARENT_STORY, RelationKind.SOURCE -> R.string.relation_kind_source
                RelationKind.PREQUEL -> R.string.relation_kind_prequel
                RelationKind.SEQUEL -> R.string.relation_kind_sequel
                RelationKind.SIDE_STORY -> R.string.relation_kind_side_story
                RelationKind.SPIN_OFF -> R.string.relation_kind_spin_off
                RelationKind.SUMMARY -> R.string.relation_kind_summary
                RelationKind.COMPILATION -> R.string.relation_kind_compilation
                RelationKind.CONTAINS -> R.string.relation_kind_contains
                else -> R.string.common_unknown
            }
        }

        fun determineSeason(date: Date): Int? {
            return when (date.month) {
                in 1..3 -> R.string.season_winter
                in 4..6 -> R.string.season_spring
                in 7..9 -> R.string.season_summer
                in 10..12 -> R.string.season_fall
                else -> null
            }
        }
    }
}