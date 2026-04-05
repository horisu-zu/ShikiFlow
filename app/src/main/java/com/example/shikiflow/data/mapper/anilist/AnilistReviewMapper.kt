package com.example.shikiflow.data.mapper.anilist

import com.example.graphql.anilist.fragment.ALReview
import com.example.graphql.anilist.fragment.ALReviewMedia
import com.example.graphql.anilist.fragment.ALReviewShort
import com.example.shikiflow.data.mapper.anilist.AnilistUserMapper.toDomain
import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.domain.model.review.ReviewMedia
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
                author = user.aLUserShort.toDomain(),
                media = media?.aLReviewMedia?.toReviewMedia(),
                likesCount = rating ?: 0,
                ratingAmount = ratingAmount ?: 0,
                createdAt = Instant.fromEpochSeconds(createdAt.toLong()),
                updatedAt = Instant.fromEpochSeconds(updatedAt.toLong())
            )
        }
    }

    fun ALReviewMedia.toReviewMedia(): ReviewMedia {
        return ReviewMedia(
            id = id,
            title = title?.romaji ?: "",
            bannerImage = bannerImage
        )
    }
}