package com.example.shikiflow.data.repository

import androidx.paging.PagingData
import com.example.shikiflow.data.datasource.ReviewDataSource
import com.example.shikiflow.di.annotations.AniList
import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.domain.model.review.ReviewRate
import com.example.shikiflow.domain.model.review.ReviewRating
import com.example.shikiflow.domain.model.review.ReviewShort
import com.example.shikiflow.domain.model.sort.ReviewType
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.repository.ReviewRepository
import com.example.shikiflow.utils.DataResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ReviewRepositoryImpl @Inject constructor(
    @param:AniList private val anilistDataSource: ReviewDataSource,
) : ReviewRepository {
    override fun getMediaReviews(
        mediaId: Int,
        mediaType: MediaType,
        sort: Sort<ReviewType>
    ): Flow<PagingData<ReviewShort>> = anilistDataSource.getMediaReviews(mediaId, mediaType, sort)

    override fun getReview(
        reviewId: Int
    ): Flow<DataResult<Review>> = anilistDataSource.getReview(reviewId)

    override suspend fun toggleReviewRating(
        reviewId: Int,
        rating: ReviewRating
    ): DataResult<ReviewRate> = anilistDataSource.toggleReviewRating(reviewId, rating)
}