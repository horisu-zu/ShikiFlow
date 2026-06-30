package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.fragment.ALReview
import com.example.graphql.anilist.fragment.ALReviewMedia
import com.example.graphql.anilist.fragment.ALReviewRate
import com.example.graphql.anilist.fragment.ALReviewShort
import com.example.graphql.anilist.type.ReviewRating as ALReviewRating
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomain
import com.example.shikiflow.data.mapper.common.MediaTitleMapper.toDomainTitle
import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.domain.model.review.ReviewMedia
import com.example.shikiflow.domain.model.review.ReviewRate
import com.example.shikiflow.domain.model.review.ReviewRating
import com.example.shikiflow.domain.model.review.ReviewShort
import kotlin.time.Instant

object AnilistReviewMapper {
    fun ALReviewShort.toDomain(): ReviewShort? {
        return user?.let { user ->
             ReviewShort(
                id = id,
                title = summary ?: "",
                score = score ?: 0,
                author = user.aLUserShort.toDomain(),
                likesCount = rating ?: 0,
                ratingAmount = ratingAmount ?: 0
            )
        }
    }

    fun ALReview.toDomain(): Review? {
        return user?.let { user ->
            Review(
                id = id,
                title = summary ?: "",
                body = body ?: "",
                score = score ?: 0,
                userRating = userRating?.toDomainReviewRating() ?: ReviewRating.NO_VOTE,
                author = user.aLUserShort.toDomain(),
                media = media?.aLReviewMedia?.toReviewMedia(),
                likesCount = rating ?: 0,
                ratingAmount = ratingAmount ?: 0,
                createdAt = Instant.fromEpochSeconds(createdAt.toLong()),
                updatedAt = Instant.fromEpochSeconds(updatedAt.toLong())
            )
        }
    }

    fun ALReviewRate.toDomainRate(): ReviewRate {
        return ReviewRate(
            id = id,
            userRating = userRating?.toDomainReviewRating() ?: ReviewRating.NO_VOTE,
            rating = rating ?: 0,
            ratingAmount = ratingAmount ?: 0
        )
    }

    fun ALReviewMedia.toReviewMedia(): ReviewMedia {
        return ReviewMedia(
            id = id,
            title = title?.mediaTitle.toDomainTitle(),
            bannerImage = bannerImage
        )
    }

    fun ALReviewRating.toDomainReviewRating(): ReviewRating {
        return when (this) {
            ALReviewRating.UP_VOTE -> ReviewRating.UP_VOTE
            ALReviewRating.DOWN_VOTE -> ReviewRating.DOWN_VOTE
            else -> ReviewRating.NO_VOTE
        }
    }

    fun ReviewRating.toAnilistReviewRating(): ALReviewRating {
        return when (this) {
            ReviewRating.NO_VOTE -> ALReviewRating.NO_VOTE
            ReviewRating.UP_VOTE -> ALReviewRating.UP_VOTE
            ReviewRating.DOWN_VOTE -> ALReviewRating.DOWN_VOTE
        }
    }
}