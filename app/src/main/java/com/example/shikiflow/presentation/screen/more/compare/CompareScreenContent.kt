package com.example.shikiflow.presentation.screen.more.compare

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.domain.model.user.ComparisonType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.mappers.MediaComparisonMapper.filterEntries
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.viewmodel.user.compare.CompareScreenUiState

@Composable
fun CompareScreenContent(
    mediaType: MediaType,
    targetUser: User,
    uiState: CompareScreenUiState,
    onMediaItemClick: (Int, MediaType) -> Unit,
    onRefresh: () -> Unit
) {
    val preferredTitleType = LocalTitleTypeController.current
    val showState = rememberSaveable {
        ComparisonType.entries.associateWith { mutableStateOf(true) }
    }

    //well, it's not exactly clean (sorting huge, non-paginated lists in the UI)
    val userRates = retain(uiState.filters, uiState.mediaUiState[uiState.mediaType], uiState.scoreFormat) {
        uiState.mediaUiState[mediaType]?.userRates?.mapValues { (_, media) ->
            media.filterEntries(uiState.filters, uiState.scoreFormat ?: ScoreFormat.POINT_10)
        } ?: emptyMap()
    }

    Box {
        if(uiState.mediaUiState[mediaType]?.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = uiState.mediaUiState[mediaType]?.errorMessage ?: stringResource(R.string.common_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { onRefresh() }
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 84.dp)
            ) {
                if(uiState.mediaUiState[mediaType]?.isLoading == true) {
                    stickyHeader {
                        MediaComparisonHeaderPlaceholder(
                            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceContainer)
                        )
                    }
                    items(count = 12) { index ->
                        MediaComparisonItemPlaceholder(
                            itemIndex = index
                        )
                    }
                } else if(uiState.mediaUiState[mediaType]?.userRates != null) {
                    userRates.forEach { (comparisonType, media) ->
                        if (media.isNotEmpty()) {
                            stickyHeader {
                                MediaComparisonHeader(
                                    currentUserNickname = uiState.currentUser?.nickname ?: "",
                                    targetUserNickname = targetUser.nickname,
                                    count = media.size,
                                    comparisonType = comparisonType,
                                    onClick = {
                                        showState[comparisonType]?.value = !(showState[comparisonType]?.value ?: true)
                                    },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .animateItem()
                                )
                            }

                            if(showState[comparisonType]?.value == true) {
                                items(
                                    count = media.size,
                                    key = { index -> media[index].id }
                                ) { index ->
                                    MediaComparisonItem(
                                        mediaItem = media[index],
                                        mediaType = mediaType,
                                        titleType = preferredTitleType,
                                        comparisonType = comparisonType,
                                        scoreFormat = uiState.scoreFormat,
                                        onItemClick = { mediaId ->
                                            onMediaItemClick(mediaId, mediaType)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .animateItem()
                                    )

                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
