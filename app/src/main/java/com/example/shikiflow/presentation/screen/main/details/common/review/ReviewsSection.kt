package com.example.shikiflow.presentation.screen.main.details.common.review

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.review.ReviewShort
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ColorMapper.getRatioColor
import com.example.shikiflow.presentation.common.mappers.UserRateIconProvider.getScoreRatioIcon
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.ignoreHorizontalParentPadding
import com.example.shikiflow.utils.toIcon
import com.materialkolor.ktx.harmonize

@Composable
fun ReviewsSection(
    reviewsList: PaginatedList<ReviewShort>,
    onMoreClick: () -> Unit,
    onReviewClick: (Int) -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextWithDivider(
                text = stringResource(R.string.details_reviews_section_label)
            )
            if(reviewsList.hasNextPage) {
                IconButton(
                    onClick = { onMoreClick() },
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }
            }
        }

        SnapFlingLazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            items(
                items = reviewsList.entries,
                key = { item -> item.id }
            ) { reviewShort ->
                ReviewShortItem(
                    review = reviewShort,
                    onReviewClick = onReviewClick,
                    modifier = Modifier
                        .width(280.dp)
                        .aspectRatio(2.25f)
                )
            }
        }
    }
}

@Composable
fun ReviewShortItem(
    review: ReviewShort,
    onReviewClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .clickable { onReviewClick(review.id) }
            .padding(all = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BaseImage(
                    model = review.author.avatarUrl,
                    imageType = ImageType.Square(
                        clip = RoundedCornerShape(percent = 16),
                        width = 24.dp
                    )
                )
                Text(
                    text = review.author.nickname,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            ReviewRatingItem(
                score = review.score,
                likesCount = review.likesCount,
                ratingAmount = review.ratingAmount
            )
        }

        Text(
            text = review.title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ReviewRatingItem(
    score: Int,
    likesCount: Int,
    ratingAmount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ReviewRatingItem(
            likesCount = likesCount,
            ratingAmount = ratingAmount,
            modifier = Modifier.fillMaxHeight()
        )

        ReviewScoreItem(
            score = score
        )
    }
}

@Composable
private fun ReviewScoreItem(
    score: Int,
    modifier: Modifier = Modifier
) {
    val scoreRatio = score / 100f
    val scoreColor = getRatioColor(scoreRatio)
        .harmonize(MaterialTheme.colorScheme.background)

    getScoreRatioIcon(scoreRatio).toIcon(
        tint = scoreColor,
        modifier = modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(
                color = scoreColor.copy(alpha = 0.2f)
            )
            .padding(all = 4.dp)
    )
}

@Composable
fun ReviewRatingItem(
    likesCount: Int,
    ratingAmount: Int,
    modifier: Modifier = Modifier
) {
    val likesRatio = likesCount / ratingAmount.toFloat()
    val ratingColor = getRatioColor(likesRatio)
        .harmonize(MaterialTheme.colorScheme.background)

    TextWithIcon(
        text = buildString {
            append(likesCount)
            append("/")
            append(ratingAmount)
        },
        iconResources = listOf(
            IconResource.Vector(imageVector = Icons.Default.Star)
        ),
        placeIconAtTheBeginning = false,
        style = MaterialTheme.typography.bodySmall.copy(
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold
        ),
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
            .clip(CircleShape)
            .background(color = ratingColor)
            .padding(
                horizontal = 6.dp,
                vertical = 4.dp
            )
    )
}