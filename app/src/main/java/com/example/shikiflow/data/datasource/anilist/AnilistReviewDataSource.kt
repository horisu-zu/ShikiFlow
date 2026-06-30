package com.example.shikiflow.data.datasource.anilist

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.apollographql.apollo.ApolloClient
import com.example.graphql.anilist.MediaReviewQuery
import com.example.graphql.anilist.MediaReviewsQuery
import com.example.graphql.anilist.RateReviewMutation
import com.example.shikiflow.data.datasource.ReviewDataSource
import com.example.shikiflow.data.local.source.GenericPagingSource
import com.example.shikiflow.data.mapper.anilist.AnilistReviewMapper.toAnilistReviewRating
import com.example.shikiflow.data.mapper.anilist.AnilistReviewMapper.toDomain
import com.example.shikiflow.data.mapper.anilist.AnilistReviewMapper.toDomainRate
import com.example.shikiflow.data.mapper.common.OrderMapper.toAnilistReviewSort
import com.example.shikiflow.di.annotations.AnilistApollo
import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.domain.model.review.ReviewRate
import com.example.shikiflow.domain.model.review.ReviewRating
import com.example.shikiflow.domain.model.review.ReviewShort
import com.example.shikiflow.domain.model.sort.ReviewType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.BaseNetworkRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AnilistReviewDataSource @Inject constructor(
    @param:AnilistApollo private val apolloClient: ApolloClient
): ReviewDataSource, BaseNetworkRepository() {
    override fun getMediaReviews(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<ReviewType>
    ): Flow<PagingData<ReviewShort>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                enablePlaceholders = true,
                prefetchDistance = 9,
                initialLoadSize = 15
            ),
            pagingSourceFactory = {
                GenericPagingSource(
                    method = { page, pageSize ->
                        paginatedMediaReviews(mediaId, sort, page, pageSize)
                    }
                )
            }
        ).flow
    }

    suspend fun paginatedMediaReviews(
        mediaId: Int,
        sort: Sort<ReviewType>,
        page: Int,
        limit: Int
    ): Result<List<ReviewShort>> {
        val reviewsQuery = MediaReviewsQuery(
            mediaId = mediaId,
            sort = sort.toAnilistReviewSort(),
            page = page,
            perPage = limit
        )

        val response = apolloClient.query(reviewsQuery).execute()

        return response.toResult().map { data ->
            data.Media
                ?.reviews
                ?.nodes
                ?.mapNotNull { reviewNode ->
                    reviewNode?.aLReviewShort?.toDomain()
                } ?: emptyList()
        }
    }

    override fun getReview(reviewId: Int): Flow<DataResult<Review>> {
        val reviewQuery = MediaReviewQuery(reviewId)

        val response = apolloClient.query(reviewQuery)
            .toFlow()
            .asDataResult { data ->
                data.Review?.aLReview?.toDomain() ?: throw NoSuchElementException("Empty Response")
            }

        return response
    }

    override suspend fun toggleReviewRating(
        reviewId: Int,
        reviewRating: ReviewRating
    ): DataResult<ReviewRate> {
        val reviewMutation = RateReviewMutation(
            reviewId = reviewId,
            rating = reviewRating.toAnilistReviewRating()
        )

        val response = apolloClient.mutation(reviewMutation)
            .execute()
            .asDataResult { data ->
                data.RateReview?.aLReviewRate?.toDomainRate() ?: throw NoSuchElementException("Empty Response")
            }

        return response
    }
}