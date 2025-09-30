package com.example.shikiflow.domain.model.mapper

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.graphql.type.AnimeKindEnum
import com.example.graphql.type.AnimeOriginEnum
import com.example.graphql.type.AnimeStatusEnum
import com.example.graphql.type.MangaKindEnum
import com.example.graphql.type.MangaStatusEnum
import com.example.graphql.type.RelationKindEnum
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.ReleaseDate
import com.example.shikiflow.domain.model.tracks.MediaType
import kotlin.reflect.KClass

object EnumUtils {
    fun formatEnumName(enumValue: Enum<*>): String =
        enumValue.name.lowercase().replace("_", " ").replaceFirstChar { it.uppercase() }

    private fun <T : Enum<T>> getEnumList(enumClass: KClass<T>): List<T> =
        enumClass.java.enumConstants
            ?.filter { !it.name.startsWith("UNKNOWN") }
            ?: emptyList()

    fun <T : Enum<T>> getFormattedEnumList(enumClass: KClass<T>): List<String> =
        getEnumList(enumClass).map { formatEnumName(it) }

    fun <T : Enum<T>> findEnumByFormattedName(enumClass: KClass<T>, formattedName: String): T? =
        getEnumList(enumClass).find { formatEnumName(it) == formattedName }
}

class UserRateMapper {
    companion object {
        fun mapStringResToStatus(resId: Int): UserRateStatusEnum? {
            return mapOf(
                R.string.media_user_status_anime_watching to UserRateStatusEnum.watching,
                R.string.media_user_status_manga_reading to UserRateStatusEnum.watching,
                R.string.media_user_status_planned to UserRateStatusEnum.planned,
                R.string.media_user_status_completed to UserRateStatusEnum.completed,
                R.string.media_user_status_anime_rewatching to UserRateStatusEnum.rewatching,
                R.string.media_user_status_manga_rereading to UserRateStatusEnum.rewatching,
                R.string.media_user_status_on_hold to UserRateStatusEnum.on_hold,
                R.string.media_user_status_dropped to UserRateStatusEnum.dropped
            )[resId]
        }

        fun simpleMapUserRateStatusToString(status: UserRateStatusEnum, mediaType: MediaType = MediaType.ANIME): Int {
            return when(status) {
                UserRateStatusEnum.watching -> if(mediaType == MediaType.ANIME) {
                   R.string.media_user_status_anime_watching
                } else { R.string.media_user_status_manga_reading }
                UserRateStatusEnum.planned -> R.string.media_user_status_planned
                UserRateStatusEnum.completed -> R.string.media_user_status_completed
                UserRateStatusEnum.rewatching -> if(mediaType == MediaType.ANIME) {
                    R.string.media_user_status_anime_rewatching
                } else { R.string.media_user_status_manga_rereading }
                UserRateStatusEnum.on_hold -> R.string.media_user_status_on_hold
                UserRateStatusEnum.dropped -> R.string.media_user_status_dropped
                UserRateStatusEnum.UNKNOWN__ -> R.string.media_user_status_unknown
            }
        }

        fun mapOriginToString(origin: AnimeOriginEnum): Int {
            return when(origin) {
                AnimeOriginEnum.original -> R.string.anime_origin_original
                AnimeOriginEnum.manga -> R.string.anime_origin_manga
                AnimeOriginEnum.web_manga -> R.string.anime_origin_web_manga
                AnimeOriginEnum.four_koma_manga -> R.string.anime_origin_4_koma_manga
                AnimeOriginEnum.novel -> R.string.anime_origin_novel
                AnimeOriginEnum.web_novel -> R.string.anime_origin_web_novel
                AnimeOriginEnum.visual_novel -> R.string.anime_origin_visual_novel
                AnimeOriginEnum.light_novel -> R.string.anime_origin_light_novel
                AnimeOriginEnum.game -> R.string.anime_origin_game
                AnimeOriginEnum.card_game -> R.string.anime_origin_card_game
                AnimeOriginEnum.music -> R.string.anime_origin_music
                AnimeOriginEnum.radio -> R.string.anime_origin_radio
                AnimeOriginEnum.book -> R.string.anime_origin_book
                AnimeOriginEnum.picture_book -> R.string.anime_origin_picture_book
                AnimeOriginEnum.mixed_media -> R.string.anime_origin_mixed_media
                AnimeOriginEnum.other -> R.string.anime_origin_other
                else -> R.string.common_unknown
            }
        }

        @Composable
        fun mapUserRateStatusToString(
            status: UserRateStatusEnum,
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
                UserRateStatusEnum.watching -> {
                    val resId = if (mediaType == MediaType.ANIME) R.string.media_user_status_anime_watching
                    else R.string.media_user_status_manga_reading
                    stringResource(resId) + progressSuffix
                }
                UserRateStatusEnum.planned -> stringResource(R.string.media_user_status_planned)
                UserRateStatusEnum.completed -> {
                    stringResource(R.string.media_user_status_completed) + scoreSuffix
                }
                UserRateStatusEnum.rewatching -> {
                    val resId = if (mediaType == MediaType.ANIME) R.string.media_user_status_anime_rewatching
                    else R.string.media_user_status_manga_rereading
                    stringResource(resId)
                }
                UserRateStatusEnum.on_hold -> {
                    stringResource(R.string.media_user_status_on_hold) + progressSuffix
                }
                UserRateStatusEnum.dropped -> {
                    val base = stringResource(R.string.media_user_status_dropped)
                    base + (scoreSuffix.takeIf { it.isNotEmpty() } ?: progressSuffix)
                }
                UserRateStatusEnum.UNKNOWN__ -> stringResource(R.string.media_user_status_unknown)
            }
        }

        fun isWatched(status: UserRateStatusEnum): Boolean {
            return setOf(
                UserRateStatusEnum.watching,
                UserRateStatusEnum.dropped,
                UserRateStatusEnum.on_hold
            ).contains(status)
        }

        fun mapAnimeStatus(status: AnimeStatusEnum?): Int {
            return when(status) {
                AnimeStatusEnum.anons -> R.string.media_status_announced
                AnimeStatusEnum.ongoing -> R.string.media_status_ongoing
                AnimeStatusEnum.released -> R.string.media_status_released
                else -> R.string.common_unknown
            }
        }

        fun mapMangaStatus(status: MangaStatusEnum?): Int {
            return when(status) {
                MangaStatusEnum.anons -> R.string.media_status_announced
                MangaStatusEnum.ongoing -> R.string.media_status_ongoing
                MangaStatusEnum.released -> R.string.media_status_released
                MangaStatusEnum.paused -> R.string.media_status_manga_paused
                MangaStatusEnum.discontinued -> R.string.media_status_manga_discontinued
                else -> R.string.common_unknown
            }
        }

        fun mapAnimeKind(status: AnimeKindEnum?): Int {
            return when(status) {
                AnimeKindEnum.tv -> R.string.anime_kind_tv
                AnimeKindEnum.ona -> R.string.anime_kind_ona
                AnimeKindEnum.ova -> R.string.anime_kind_ova
                AnimeKindEnum.cm -> R.string.anime_kind_cm
                AnimeKindEnum.pv -> R.string.anime_kind_pv
                AnimeKindEnum.movie -> R.string.anime_kind_movie
                AnimeKindEnum.music -> R.string.anime_kind_music
                AnimeKindEnum.special -> R.string.anime_kind_special
                AnimeKindEnum.tv_special -> R.string.anime_kind_tv_special
                else -> R.string.common_unknown
            }
        }

        fun mapMangaKind(kind: MangaKindEnum?): Int {
            return when(kind) {
                MangaKindEnum.manga -> R.string.manga_kind_manga
                MangaKindEnum.manhwa -> R.string.manga_kind_manhwa
                MangaKindEnum.manhua -> R.string.manga_kind_manhua
                MangaKindEnum.light_novel -> R.string.manga_kind_ranobe
                MangaKindEnum.novel -> R.string.manga_kind_novel
                MangaKindEnum.one_shot -> R.string.manga_kind_one_shot
                MangaKindEnum.doujin -> R.string.manga_kind_doujin
                else -> R.string.common_unknown
            }
        }

        fun mapRelationKind(kind: RelationKindEnum?): Int {
            return when(kind) {
                RelationKindEnum.adaptation -> R.string.relation_kind_adaptation
                RelationKindEnum.alternative_setting -> R.string.relation_kind_alternative_setting
                RelationKindEnum.alternative_version -> R.string.relation_kind_alternative_version
                RelationKindEnum.character -> R.string.relation_kind_character
                RelationKindEnum.full_story -> R.string.relation_kind_full_story
                RelationKindEnum.other -> R.string.relation_kind_other
                RelationKindEnum.parent_story -> R.string.relation_kind_parent_story
                RelationKindEnum.prequel -> R.string.relation_kind_prequel
                RelationKindEnum.sequel -> R.string.relation_kind_sequel
                RelationKindEnum.side_story -> R.string.relation_kind_side_story
                RelationKindEnum.spin_off -> R.string.relation_kind_spin_off
                RelationKindEnum.summary -> R.string.relation_kind_summary
                else -> R.string.common_unknown
            }
        }

        fun determineSeason(releaseDate: ReleaseDate): Int {
            val month = releaseDate.month

            return when (month) {
                in 3..5 -> R.string.season_spring
                in 6..8 -> R.string.season_summer
                in 9..11 -> R.string.season_fall
                in listOf(1, 2, 12) -> R.string.season_winter
                else -> R.string.common_unknown
            }
        }
    }
}