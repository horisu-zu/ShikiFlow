package com.example.shikiflow.presentation.screen.more.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.domain.model.track.UserRateStatus
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.ComparisonType
import com.example.shikiflow.domain.model.user.MediaComparison
import com.example.shikiflow.domain.model.user.ShortUserRateData
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.UserRateStatusMapper.mapStatus
import com.example.shikiflow.presentation.viewmodel.user.compare.CompareScreenViewModel

@Composable
fun CompareScreenContent(
    mediaType: MediaType,
    targetUser: User,
    onMediaItemClick: (Int, MediaType) -> Unit,
    compareScreenViewModel: CompareScreenViewModel = hiltViewModel()
) {
    val uiState by compareScreenViewModel.uiState.collectAsStateWithLifecycle()
    val showState = rememberSaveable {
        ComparisonType.entries.associateWith { mutableStateOf(true) }
    }

    LaunchedEffect(mediaType) {
        compareScreenViewModel.setData(targetUser.id, mediaType)
    }

    Box {
        if(uiState.mediaUiState[mediaType]?.isLoading == true) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(uiState.mediaUiState[mediaType]?.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = uiState.mediaUiState[mediaType]?.errorMessage ?: stringResource(R.string.common_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { compareScreenViewModel.onRefresh(mediaType) }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                uiState.mediaUiState[mediaType]?.userRates?.let { userRatesMap ->
                    userRatesMap.forEach { (comparisonType, media) ->
                        stickyHeader {
                            CompareHeader(
                                currentUserNickname = uiState.currentUser?.nickname ?: "",
                                targetUserNickname = targetUser.nickname,
                                count = media.size,
                                comparisonType = comparisonType,
                                onClick = {
                                    showState[comparisonType]?.value = !(showState[comparisonType]?.value ?: true)
                                },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .animateItem()
                            )
                        }
                        if(showState[comparisonType]?.value == true) {
                            items(
                                count = media.size,
                                key = { index -> media[index].id }
                            ) { index ->
                                ComparisonItem(
                                    mediaItem = media[index],
                                    mediaType = mediaType,
                                    currentUserScore = media[index].currentUserScore,
                                    targetUserScore = media[index].targetUserScore,
                                    comparisonType = comparisonType,
                                    onItemClick = { mediaId ->
                                        onMediaItemClick(mediaId, mediaType)
                                    },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.background)
                                        .animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CompareHeader(
    currentUserNickname: String,
    targetUserNickname: String,
    count: Int,
    comparisonType: ComparisonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildString {
                    append(
                        when(comparisonType) {
                            ComparisonType.SHARED -> stringResource(R.string.compare_shared_rates)
                            ComparisonType.CURRENT_USER_ONLY -> stringResource(
                                R.string.compare_unique_rates,
                                currentUserNickname
                            )
                            ComparisonType.TARGET_USER_ONLY -> stringResource(
                                R.string.compare_unique_rates,
                                targetUserNickname
                            )
                        }
                    )
                    append(" — $count")
                },
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(2f)
            )
            VerticalDivider(
                color = MaterialTheme.colorScheme.background,
                thickness = 2.dp,
                modifier = Modifier.fillMaxHeight()
            )
            if(comparisonType != ComparisonType.TARGET_USER_ONLY) {
                Text(
                    text = currentUserNickname,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            if(comparisonType == ComparisonType.SHARED) {
                VerticalDivider(
                    color = MaterialTheme.colorScheme.background,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxHeight()
                )
            }
            if(comparisonType != ComparisonType.CURRENT_USER_ONLY) {
                Text(
                    text = targetUserNickname,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
private fun ComparisonItem(
    mediaItem: MediaComparison,
    mediaType: MediaType,
    currentUserScore: ShortUserRateData?,
    targetUserScore: ShortUserRateData?,
    comparisonType: ComparisonType,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            Row(
                modifier = Modifier
                    .weight(2f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onItemClick(mediaItem.id) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
            ) {
                BaseImage(
                    model = mediaItem.imageUrl,
                    contentDescription = "Media Image",
                    imageType = ImageType.Poster(defaultWidth = 48.dp)
                )
                Text(
                    text = mediaItem.title,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            VerticalDivider(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                thickness = 2.dp,
                modifier = Modifier.fillMaxHeight()
            )
            if(comparisonType != ComparisonType.TARGET_USER_ONLY) {
                Text(
                    text = if(
                        currentUserScore?.status == UserRateStatus.COMPLETED ||
                        currentUserScore?.status == UserRateStatus.WATCHING
                    ) {
                        when(currentUserScore.userScore) {
                            0 -> { "-" }
                            else -> currentUserScore.userScore.toString()
                        }
                    } else {
                        stringResource(
                            id = (currentUserScore?.status ?:  UserRateStatus.PLANNED).mapStatus(
                                mediaType = mediaType
                            )
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
            if(comparisonType == ComparisonType.SHARED) {
                VerticalDivider(
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxHeight()
                )
            }
            if(comparisonType != ComparisonType.CURRENT_USER_ONLY) {
                Text(
                    text = if(
                        targetUserScore?.status == UserRateStatus.COMPLETED ||
                        targetUserScore?.status == UserRateStatus.WATCHING
                    ) {
                        when(targetUserScore.userScore) {
                            0 -> { "-" }
                            else -> targetUserScore.userScore.toString()
                        }
                    } else {
                        stringResource(
                            id = (targetUserScore?.status ?:  UserRateStatus.PLANNED).mapStatus(
                                mediaType = mediaType
                            )
                        )
                    },
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        HorizontalDivider()
    }
}