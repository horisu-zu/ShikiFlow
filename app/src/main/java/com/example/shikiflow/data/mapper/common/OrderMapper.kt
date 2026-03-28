package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.CharacterSort
import com.example.graphql.anilist.type.MediaListSort
import com.example.graphql.anilist.type.StaffSort
import com.example.graphql.anilist.type.MediaSort as ALMediaSort
import com.example.graphql.shikimori.type.OrderEnum
import com.example.graphql.shikimori.type.SortOrderEnum
import com.example.graphql.shikimori.type.UserRateOrderFieldEnum
import com.example.graphql.shikimori.type.UserRateOrderInputType
import com.example.shikiflow.domain.model.sort.CharacterType
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.StaffType

object OrderMapper {
    fun Sort<MediaSort>.toAnilistMediaSort(): ALMediaSort {
        return when(type) {
            MediaSort.Common.POPULARITY -> when(direction) {
                SortDirection.ASCENDING -> ALMediaSort.POPULARITY
                SortDirection.DESCENDING -> ALMediaSort.POPULARITY_DESC
            }
            MediaSort.Common.SCORE -> when(direction) {
                SortDirection.ASCENDING -> ALMediaSort.SCORE
                SortDirection.DESCENDING -> ALMediaSort.SCORE_DESC
            }
            MediaSort.Anilist.TRENDING -> when(direction) {
                SortDirection.ASCENDING -> ALMediaSort.TRENDING
                SortDirection.DESCENDING -> ALMediaSort.TRENDING_DESC
            }
            MediaSort.Anilist.FAVORITES -> when(direction) {
                SortDirection.ASCENDING -> ALMediaSort.FAVOURITES
                SortDirection.DESCENDING -> ALMediaSort.FAVOURITES_DESC
            }
            MediaSort.Anilist.DATE_ADDED -> when(direction) {
                SortDirection.ASCENDING -> ALMediaSort.START_DATE
                SortDirection.DESCENDING -> ALMediaSort.START_DATE_DESC
            }
            MediaSort.Anilist.RELEASE_DATE -> when(direction) {
                SortDirection.ASCENDING -> ALMediaSort.END_DATE
                SortDirection.DESCENDING -> ALMediaSort.END_DATE_DESC
            }
            else -> ALMediaSort.SEARCH_MATCH
        }
    }

    fun Sort<UserRateType>.toAnilistOrder(): MediaListSort {
        return when(type) {
            UserRateType.ID -> when(direction) {
                SortDirection.ASCENDING -> MediaListSort.MEDIA_ID
                SortDirection.DESCENDING -> MediaListSort.MEDIA_ID_DESC
            }
            UserRateType.ADDED_AT -> when(direction) {
                SortDirection.ASCENDING -> MediaListSort.ADDED_TIME
                SortDirection.DESCENDING -> MediaListSort.ADDED_TIME_DESC
            }
            UserRateType.UPDATED_AT -> when(direction) {
                SortDirection.ASCENDING -> MediaListSort.UPDATED_TIME
                SortDirection.DESCENDING -> MediaListSort.UPDATED_TIME_DESC
            }
            UserRateType.SCORE -> when(direction) {
                SortDirection.ASCENDING -> MediaListSort.SCORE
                SortDirection.DESCENDING -> MediaListSort.SCORE_DESC
            }
            UserRateType.PROGRESS -> when(direction) {
                SortDirection.ASCENDING -> MediaListSort.PROGRESS
                SortDirection.DESCENDING -> MediaListSort.PROGRESS_DESC
            }
        }
    }

    fun Sort<StaffType>.toAnilistStaffSort(): StaffSort {
        return when(type) {
            StaffType.ID ->  when(direction) {
                SortDirection.ASCENDING -> StaffSort.ID
                SortDirection.DESCENDING -> StaffSort.ID_DESC
            }
            StaffType.ROLE -> when(direction) {
                SortDirection.ASCENDING -> StaffSort.ROLE
                SortDirection.DESCENDING -> StaffSort.ROLE_DESC
            }
            StaffType.FAVORITES -> when(direction) {
                SortDirection.ASCENDING -> StaffSort.FAVOURITES
                SortDirection.DESCENDING -> StaffSort.FAVOURITES_DESC
            }
            StaffType.RELEVANCE -> StaffSort.RELEVANCE
        }
    }

    fun Sort<CharacterType>.toAnilistCharacterSort(): CharacterSort {
        return when(type) {
            CharacterType.RELEVANCE -> CharacterSort.RELEVANCE
            CharacterType.FAVORITES -> when(direction) {
                SortDirection.ASCENDING -> CharacterSort.FAVOURITES
                SortDirection.DESCENDING -> CharacterSort.FAVOURITES_DESC
            }
            CharacterType.ROLE -> when(direction) {
                SortDirection.ASCENDING -> CharacterSort.ROLE
                SortDirection.DESCENDING -> CharacterSort.ROLE_DESC
            }
            CharacterType.ID -> when(direction) {
                SortDirection.ASCENDING -> CharacterSort.ID
                SortDirection.DESCENDING -> CharacterSort.ID_DESC
            }
        }
    }

    fun Sort<UserRateType>.toShikimoriOrder(): UserRateOrderInputType {
        val orderType = when(type) {
            UserRateType.ID -> UserRateOrderFieldEnum.id
            UserRateType.UPDATED_AT -> UserRateOrderFieldEnum.updated_at
            else -> UserRateOrderFieldEnum.UNKNOWN__
        }

        return UserRateOrderInputType(
            field = orderType,
            order = this.direction.toShikimoriOrder()
        )
    }

    fun Sort<UserRateType>.toShikimoriOrderEnum(): OrderEnum {
        return when(type) {
            UserRateType.ID -> OrderEnum.id_desc
            UserRateType.ADDED_AT -> OrderEnum.created_at_desc
            UserRateType.UPDATED_AT -> OrderEnum.aired_on
            UserRateType.SCORE -> OrderEnum.ranked
            UserRateType.PROGRESS -> OrderEnum.episodes
        }
    }

    fun SortDirection.toShikimoriOrder(): SortOrderEnum {
        return when(this) {
            SortDirection.ASCENDING -> SortOrderEnum.asc
            SortDirection.DESCENDING -> SortOrderEnum.desc
        }
    }

    fun SortType.toShikimoriBrowseOrder(): OrderEnum {
        return when(this) {
            MediaSort.Common.POPULARITY -> OrderEnum.popularity
            MediaSort.Shikimori.RANKED -> OrderEnum.ranked_shiki
            MediaSort.Common.SCORE -> OrderEnum.ranked
            MediaSort.Shikimori.EPISODES -> OrderEnum.episodes
            MediaSort.Shikimori.STATUS -> OrderEnum.status
            else -> OrderEnum.UNKNOWN__
        }
    }

    fun SortType.toAnilistBrowseOrder(): ALMediaSort {
        return when(this) {
            MediaSort.Common.POPULARITY -> ALMediaSort.POPULARITY_DESC
            MediaSort.Common.SCORE -> ALMediaSort.SCORE_DESC
            MediaSort.Anilist.TRENDING -> ALMediaSort.TRENDING_DESC
            MediaSort.Anilist.FAVORITES -> ALMediaSort.FAVOURITES_DESC
            MediaSort.Anilist.DATE_ADDED -> ALMediaSort.START_DATE_DESC
            MediaSort.Anilist.RELEASE_DATE -> ALMediaSort.END_DATE_DESC
            else -> ALMediaSort.UNKNOWN__
        }
    }
}