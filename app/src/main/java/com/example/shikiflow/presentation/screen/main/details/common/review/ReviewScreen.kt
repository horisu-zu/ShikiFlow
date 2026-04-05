package com.example.shikiflow.presentation.screen.main.details.common.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.review.Review
import com.example.shikiflow.presentation.WindowSize
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.ColorMapper.getRatioColor
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.media.review.ReviewViewModel
import com.example.shikiflow.utils.Converter.formatInstant
import com.example.shikiflow.utils.IconResource
import com.example.shikiflow.utils.WebIntent
import com.materialkolor.ktx.harmonize

@Composable
fun ReviewScreen(
    reviewId: Int,
    navOptions: MediaNavOptions,
    reviewViewModel: ReviewViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by reviewViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(reviewId) {
        reviewViewModel.setId(reviewId)
    }

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
                    ExpandableText(
                        htmlText = review.body,
                        authType = AuthType.ANILIST,
                        modifier = Modifier.fillMaxWidth(),
                        collapsedMaxLines = Int.MAX_VALUE,
                        style = MaterialTheme.typography.bodySmall,
                        linkColor = MaterialTheme.colorScheme.primary,
                        brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
                        onEntityClick = { entityType, id ->
                            navOptions.navigateByEntity(entityType, id)
                        },
                        onLinkClick = { url ->
                            WebIntent.openUrlCustomTab(context, url)
                        }
                    )
                }

                item {
                    ReviewScoreComponent(
                        score = review.score
                    )
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
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val windowSize by remember(windowSizeClass) {
        derivedStateOf {
            WindowSize.from(windowSizeClass)
        }
    }

    val boxItemPadding = 12.dp
    val backgroundColor = MaterialTheme.colorScheme.background.copy(alpha = 0.35f)

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
                    clip = RoundedCornerShape(percent = 8),
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
                append(review.media?.title)
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

        ReviewScoreComponent(
            score = review.score,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(all = boxItemPadding)
        )
    }
}

@Composable
private fun ReviewScoreComponent(
    score: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = buildAnnotatedString {
            append(score.toString())
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
        style = MaterialTheme.typography.titleSmall,
        modifier = modifier
            .clip(RoundedCornerShape(percent = 16))
            .background(
                color = getRatioColor(score / 100f)
                    .harmonize(MaterialTheme.colorScheme.background)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
    )
}