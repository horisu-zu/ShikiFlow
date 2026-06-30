package com.example.shikiflow.presentation.screen.main.details.common.review

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.domain.model.review.ReviewRating
import com.example.shikiflow.presentation.WindowSize
import com.example.shikiflow.presentation.common.DigitCounter
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.RichTextRenderer
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ColorMapper.getRatioColor
import com.example.shikiflow.presentation.common.mappers.ColorMapper.onColor
import com.example.shikiflow.presentation.common.mappers.UserRateIconProvider.getScoreRatioIcon
import com.example.shikiflow.presentation.common.player.LocalExoPlayerCache
import com.example.shikiflow.presentation.common.player.rememberExoPlayerCache
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.media.review.ReviewViewModel
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.toIcon
import com.materialkolor.ktx.harmonize

@Composable
fun ReviewScreen(
    reviewId: Int,
    navOptions: MediaNavOptions,
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val uiState by reviewViewModel.uiState.collectAsStateWithLifecycle()
    val playerCache = rememberExoPlayerCache()

    LaunchedEffect(reviewId) {
        reviewViewModel.setId(reviewId)
    }

    CompositionLocalProvider(
        LocalExoPlayerCache provides playerCache
    ) {
        LazyColumn(
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = WindowInsets.systemBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            if(uiState.isLoading) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator() }
                }
            } else if(uiState.errorMessage != null) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ErrorItem(
                            message = uiState.errorMessage ?: stringResource(id = R.string.common_error),
                            buttonLabel = stringResource(R.string.common_retry),
                            onButtonClick = { reviewViewModel.onRefresh() }
                        )
                    }
                }
            } else {
                uiState.review?.let { review ->
                    item {
                        ReviewHeader(
                            review = review,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        RichTextRenderer(
                            htmlText = review.body,
                            modifier = Modifier.fillMaxWidth(),
                            style = MaterialTheme.typography.bodySmall,
                            linkColor = MaterialTheme.colorScheme.primary,
                            onEntityClick = { entityType, id ->
                                navOptions.navigateByEntity(entityType, id)
                            }
                        )
                    }

                    item {
                        ReviewScoreComponent(
                            score = review.score,
                            likesCount = review.likesCount,
                            ratingAmount = review.ratingAmount,
                            userRating = review.userRating,
                            onRatingClick = { rating, isUserRating ->
                                reviewViewModel.toggleRating(reviewId,rating, isUserRating)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewHeader(
    review: Review,
    modifier: Modifier = Modifier
) {
    val titleType = LocalTitleTypeController.current
    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass
    val windowSize by remember(windowSizeClass) {
        derivedStateOf {
            WindowSize.from(windowSizeClass)
        }
    }

    val boxItemPadding = 12.dp
    val backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.35f)
    val scoreRatioColor = getRatioColor(review.score / 100f)
        .harmonize(MaterialTheme.colorScheme.background)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        review.media?.bannerImage?.let { bannerImage ->
            BaseImage(
                model = bannerImage,
                contentScale = ContentScale.Crop,
                imageType = ImageType.Screenshot(
                    width = Int.MAX_VALUE.dp,
                    shape = RoundedCornerShape(percent = 8),
                    aspectRatio = when(windowSize) {
                        WindowSize.COMPACT -> 16f / 9f
                        else -> 3.2f
                    }
                ),
                modifier = Modifier.alpha(0.45f)
            )
        }

        Text(
            text = buildAnnotatedString {
                append(review.media?.title?.preferred(titleType))
                append("\n")
                withStyle(
                    style = SpanStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append(
                        stringResource(R.string.review_author_prefix)
                    )
                }
                withStyle(
                    style = SpanStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append(review.author.nickname)
                }
            },
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .widthIn(max = 288.dp)
                .clip(RoundedCornerShape(percent = 24))
                .background(backgroundColor)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(all = boxItemPadding)
                .clip(RoundedCornerShape(percent = 16))
                .background(backgroundColor)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            TextWithIcon(
                text = formatInstant(
                    instant = review.createdAt,
                    includeTime = true
                ),
                iconResources = listOf(
                    IconResource.Vector(Icons.Default.Done)
                ),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp
                )
            )
            if(review.createdAt != review.updatedAt) {
                TextWithIcon(
                    text = formatInstant(
                        instant = review.updatedAt,
                        includeTime = true
                    ),
                    iconResources = listOf(
                        IconResource.Vector(Icons.Default.Edit)
                    ),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp
                    )
                )
            }
        }

        ReviewRatingItem(
            likesCount = review.likesCount,
            ratingAmount = review.ratingAmount,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(all = boxItemPadding)
        )

        Text(
            text = buildAnnotatedString {
                append(review.score.toString())
                withStyle(
                    style = SpanStyle(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Light
                    )
                ) {
                    append("/100")
                }
            },
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall.copy(
                color = scoreRatioColor.onColor()
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(all = boxItemPadding)
                .clip(RoundedCornerShape(percent = 16))
                .background(scoreRatioColor)
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ReviewScoreComponent(
    score: Int,
    likesCount: Int,
    ratingAmount: Int,
    userRating: ReviewRating,
    onRatingClick: (ReviewRating, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val ratioColor = getRatioColor(score / 100f)
        .harmonize(MaterialTheme.colorScheme.background)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserScoreComponent(
            reviewRating = ReviewRating.UP_VOTE,
            userRating = userRating,
            score = likesCount,
            color = getRatioColor(1f)
                .harmonize(MaterialTheme.colorScheme.background),
            iconResource = getScoreRatioIcon(1f),
            onRatingClick = onRatingClick
        )

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Max)
                .clip(RoundedCornerShape(percent = 16))
                .background(MaterialTheme.colorScheme.secondaryContainer),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.ongoing_browse_mode_score),
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                ),
                modifier = Modifier.padding(start = 12.dp)
            )

            Column(
                modifier = Modifier
                    .width(64.dp)
                    .clip(RoundedCornerShape(topStartPercent = 16, bottomStartPercent = 16))
                    .background(ratioColor)
                    .padding(all = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ratioColor.onColor()
                    )
                )

                HorizontalDivider(
                    color = ratioColor.onColor()
                )

                Text(
                    text = "100",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = ratioColor.onColor()
                    )
                )
            }
        }

        UserScoreComponent(
            reviewRating = ReviewRating.DOWN_VOTE,
            userRating = userRating,
            score = ratingAmount - likesCount,
            iconResource = getScoreRatioIcon(1f / 3f),
            color = getRatioColor(1f / 3f)
                .harmonize(MaterialTheme.colorScheme.background),
            onRatingClick = onRatingClick
        )
    }
}

@Composable
private fun UserScoreComponent(
    reviewRating: ReviewRating,
    userRating: ReviewRating,
    score: Int,
    iconResource: IconResource,
    color: Color,
    onRatingClick: (ReviewRating, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUserRating = userRating == reviewRating

    Row(
        modifier = modifier
            .animateContentSize()
            .clip(RoundedCornerShape(percent = 32))
            .background(color.copy(alpha = 0.2f))
            .padding(all = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        iconResource.toIcon(
            tint = color,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(percent = 24))
                .clickable { onRatingClick(reviewRating, isUserRating) }
                .then(
                    if (isUserRating) {
                        Modifier.background(color.copy(alpha = 0.5f))
                    } else Modifier
                )
                .padding(all = 4.dp)
        )

        DigitCounter(
            count = score,
            style = MaterialTheme.typography.titleSmall.copy(
                color = color
            )
        )
    }
}