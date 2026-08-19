package com.example.shikiflow.presentation.screen.more.compare

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.common.ScoreFormat
import com.example.shikiflow.presentation.common.ConnectedButtonGroup
import com.example.shikiflow.presentation.common.CustomTextField
import com.example.shikiflow.presentation.common.GenresFilterComponent
import com.example.shikiflow.presentation.common.TagsFilterComponent
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.floatingPointRange
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.steps
import com.example.shikiflow.presentation.common.mappers.ScoreFormatMapper.valueRange
import com.example.shikiflow.presentation.screen.browse.main.GenreType
import com.example.shikiflow.presentation.screen.browse.main.GenreType.Companion.tabRowItem
import com.example.shikiflow.presentation.viewmodel.user.compare.CompareMediaFilters
import com.example.shikiflow.utils.IconResource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompareSortBottomSheet(
    authType: AuthType,
    currentUserName: String,
    targetUserName: String,
    scoreFormat: ScoreFormat,
    filters: CompareMediaFilters,
    onFiltersChange: (CompareMediaFilters) -> Unit,
    onDismiss: () -> Unit,
) {
    var genreType by remember { mutableStateOf(GenreType.GENRE) }
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )

    ModalBottomSheet(
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        ) {
            TitleSearchComponent(
                query = filters.query,
                onQueryChange = { query ->
                    onFiltersChange(
                        filters.copy(
                            query = query
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            ScoreRangeSliderComponent(
                username = currentUserName,
                scoreFormat = scoreFormat,
                scoreRange = filters.currentUserScoreRange ?: scoreFormat.valueRange().floatingPointRange(),
                onRangeChange = { newRange ->
                    onFiltersChange(
                        filters.copy(
                            currentUserScoreRange = newRange
                        )
                    )
                }
            )

            ScoreRangeSliderComponent(
                username = targetUserName,
                scoreFormat = scoreFormat,
                scoreRange = filters.targetUserScoreRange ?: scoreFormat.valueRange().floatingPointRange(),
                onRangeChange = { newRange ->
                    onFiltersChange(
                        filters.copy(
                            targetUserScoreRange = newRange
                        )
                    )
                }
            )

            //Shikimori API response doesn't provide genres or tags for target user's rates,
            //but I decided to keep these filter options anyway
            ConnectedButtonGroup(
                items = GenreType.entries.map { genreType ->
                    genreType.tabRowItem()
                },
                selectedIndex = genreType.ordinal,
                onItemSelection = { index ->
                    genreType = GenreType.entries[index]
                },
                showText = true,
                textStyle = MaterialTheme.typography.bodySmall,
                iconSize = IconButtonDefaults.extraSmallIconSize
            )

            AnimatedContent(
                targetState = genreType,
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { genreType ->
                when (genreType) {
                    GenreType.GENRE -> {
                        GenresFilterComponent(
                            authType = authType,
                            selectedGenres = filters.selectedGenres,
                            onGenreClick = { genre ->
                                onFiltersChange(
                                    filters.copy(
                                        selectedGenres =  if (filters.selectedGenres.contains(genre)) {
                                            filters.selectedGenres - genre
                                        } else filters.selectedGenres + genre
                                    )
                                )
                            }
                        )
                    }
                    GenreType.TAG -> {
                        TagsFilterComponent(
                            authType = authType,
                            selectedTags = filters.selectedTags,
                            onTagClick = { tag ->
                                onFiltersChange(
                                    filters.copy(
                                        selectedTags =  if (filters.selectedTags.contains(tag)) {
                                            filters.selectedTags - tag
                                        } else filters.selectedTags + tag
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(FlowPreview::class)
@Composable
private fun TitleSearchComponent(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val textFieldState = rememberTextFieldState(
        initialText = query
    )

    LaunchedEffect(textFieldState.text) {
        snapshotFlow { textFieldState.text }
            .debounce(500.milliseconds)
            .collect { query -> onQueryChange(query.toString()) }
    }

    CustomTextField(
        textFieldState = textFieldState,
        placeholder = {
            TextWithIcon(
                text = stringResource(R.string.compare_title_search_label),
                iconResources = listOf(
                    IconResource.Vector(imageVector = Icons.Default.Search)
                ),
                placeIconAtTheBeginning = true,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = modifier
            .clip(RoundedCornerShape(percent = 16))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    )
}

@Composable
private fun ScoreRangeSliderComponent(
    username: String,
    scoreFormat: ScoreFormat,
    scoreRange: ClosedFloatingPointRange<Float>,
    onRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.compare_score_range, username),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = scoreFormat.displayValue(scoreRange.start),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier
                        .width(48.dp)
                        .clip(RoundedCornerShape(percent = 24))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(all = 8.dp)
                )

                Text(
                    text = "..",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = scoreFormat.displayValue(scoreRange.endInclusive),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    modifier = Modifier
                        .width(48.dp)
                        .clip(RoundedCornerShape(percent = 24))
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(all = 8.dp)
                )
            }
        }

        RangeSlider(
            value = scoreRange,
            steps = scoreFormat.valueRange().steps(),
            valueRange = scoreFormat.valueRange().floatingPointRange(),
            onValueChange = { newRange ->
                onRangeChange(newRange)
            },
            modifier = Modifier.height(24.dp)
        )
    }
}