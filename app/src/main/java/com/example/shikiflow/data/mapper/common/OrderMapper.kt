package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.MediaListSort
import com.example.graphql.anilist.type.MediaSort
import com.example.graphql.shikimori.type.OrderEnum
import com.example.graphql.shikimori.type.SortOrderEnum
import com.example.graphql.shikimori.type.UserRateOrderFieldEnum
import com.example.graphql.shikimori.type.UserRateOrderInputType
import com.example.shikiflow.domain.model.common.SortDirection
import com.example.shikiflow.domain.model.track.BrowseOrder
import com.example.shikiflow.domain.model.track.UserRateOrder
import com.example.shikiflow.domain.model.track.OrderOption
import com.example.shikiflow.domain.model.track.UserRateOrderType

object OrderMapper {

    fun UserRateOrder.toAnilistOrder(): MediaListSort {
        return when(type) {
            UserRateOrderType.ID -> when(sort) {
                SortDirection.ASCENDING -> MediaListSort.MEDIA_ID
                SortDirection.DESCENDING -> MediaListSort.MEDIA_ID_DESC
            }
            UserRateOrderType.ADDED_AT -> when(sort) {
                SortDirection.ASCENDING -> MediaListSort.ADDED_TIME
                SortDirection.DESCENDING -> MediaListSort.ADDED_TIME_DESC
            }
            UserRateOrderType.UPDATED_AT -> when(sort) {
                SortDirection.ASCENDING -> MediaListSort.UPDATED_TIME
                SortDirection.DESCENDING -> MediaListSort.UPDATED_TIME_DESC
            }
            UserRateOrderType.SCORE -> when(sort) {
                SortDirection.ASCENDING -> MediaListSort.SCORE
                SortDirection.DESCENDING -> MediaListSort.SCORE_DESC
            }
            UserRateOrderType.PROGRESS -> when(sort) {
                SortDirection.ASCENDING -> MediaListSort.PROGRESS
                SortDirection.DESCENDING -> MediaListSort.PROGRESS_DESC
            }
        }
    }

    fun UserRateOrder.toShikimoriOrder(): UserRateOrderInputType {
        val orderType = when(type) {
            UserRateOrderType.ID -> UserRateOrderFieldEnum.id
            UserRateOrderType.UPDATED_AT -> UserRateOrderFieldEnum.updated_at
            else -> UserRateOrderFieldEnum.UNKNOWN__
        }

        return UserRateOrderInputType(
            field = orderType,
            order = this.sort.toShikimoriOrder()
        )
    }

    fun UserRateOrderType.toShikimoriOrder(): OrderEnum {
        return when(this) {
            UserRateOrderType.ID -> OrderEnum.id_desc
            UserRateOrderType.ADDED_AT -> OrderEnum.created_at_desc
            UserRateOrderType.UPDATED_AT -> OrderEnum.aired_on
            UserRateOrderType.SCORE -> OrderEnum.ranked
            UserRateOrderType.PROGRESS -> OrderEnum.episodes
        }
    }

    fun SortDirection.toShikimoriOrder(): SortOrderEnum {
        return when(this) {
            SortDirection.ASCENDING -> SortOrderEnum.asc
            SortDirection.DESCENDING -> SortOrderEnum.desc
        }
    }

    fun OrderOption.toShikimoriBrowseOrder(): OrderEnum {
        return when(this) {
            BrowseOrder.Shikimori.POPULARITY -> OrderEnum.popularity
            BrowseOrder.Shikimori.RANKED -> OrderEnum.ranked_shiki
            BrowseOrder.Shikimori.RANKED_MAL -> OrderEnum.ranked
            BrowseOrder.Shikimori.EPISODES -> OrderEnum.episodes
            BrowseOrder.Shikimori.STATUS -> OrderEnum.status
            else -> OrderEnum.UNKNOWN__
        }
    }

    fun OrderOption.toAnilistBrowseOrder(): MediaSort {
        return when(this) {
            BrowseOrder.Anilist.POPULARITY -> MediaSort.POPULARITY_DESC
            BrowseOrder.Anilist.SCORE -> MediaSort.SCORE_DESC
            BrowseOrder.Anilist.TRENDING -> MediaSort.TRENDING_DESC
            BrowseOrder.Anilist.FAVORITES -> MediaSort.FAVOURITES_DESC
            BrowseOrder.Anilist.DATE_ADDED -> MediaSort.UPDATED_AT
            BrowseOrder.Anilist.RELEASE_DATE -> MediaSort.END_DATE_DESC
            else -> MediaSort.UNKNOWN__
        }
    }
}