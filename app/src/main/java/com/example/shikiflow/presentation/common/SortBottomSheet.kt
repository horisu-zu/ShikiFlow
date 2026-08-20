package com.example.shikiflow.presentation.common

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.domain.model.sort.UserRateType
import com.example.shikiflow.presentation.common.mappers.GenreMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.SortMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.TagMapper.displayValue
import com.example.shikiflow.presentation.screen.main.TracksFilterType
import com.example.shikiflow.presentation.screen.main.TracksFilterType.Companion.tabRowItem
import com.example.shikiflow.presentation.viewmodel.anime.tracks.search.TracksFilters
import com.example.shikiflow.utils.IconResource

data class SortConfig<T : SortType>(
    val options: List<T>,
    val selected: Sort<T>,
    val onSortChange: (Sort<T>) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : SortType> SortBottomSheet(
    config: SortConfig<T>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )

    ModalBottomSheet(
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = { onDismiss() }
    ) {
        SortByComponent(
            config = config,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TracksSortBottomSheet(
    authType: AuthType,
    tracksFilters: TracksFilters,
    customLists: List<String>,
    config: SortConfig<UserRateType>,
    onFilterTypeChange: (TracksFilterType) -> Unit,
    onGenreChange: (Genre) -> Unit,
    onTagChange: (MediaTagEnum) -> Unit,
    onCustomListChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val horizontalPadding = 16.dp
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
            modifier = Modifier.padding(horizontal = horizontalPadding, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            ScrollableConnectedButtonGroup(
                items = TracksFilterType.entries.filter { type ->
                    if (authType == AuthType.SHIKIMORI) {
                        type != TracksFilterType.CUSTOM_LISTS
                    } else true
                }. map { filterType ->
                    filterType.tabRowItem()
                },
                selectedIndex = tracksFilters.currentFilterType.ordinal,
                onItemSelection = { index ->
                    onFilterTypeChange(TracksFilterType.entries[index])
                },
                showText = true,
                textStyle = MaterialTheme.typography.bodySmall,
                iconSize = IconButtonDefaults.extraSmallIconSize,
                paddingValues = PaddingValues(horizontal = horizontalPadding),
                modifier = Modifier
                    .ignoreHorizontalParentPadding(horizontalPadding)
                    .fillMaxWidth()
            )

            AnimatedContent(
                targetState = tracksFilters.currentFilterType,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.animateContentSize()
            ) { filterType ->
                when(filterType) {
                    TracksFilterType.SORT -> {
                        SortByComponent(config)
                    }
                    TracksFilterType.GENRES -> {
                        GenresFilterComponent(
                            authType = authType,
                            selectedGenres = tracksFilters.genres,
                            onGenreClick = onGenreChange
                        )
                    }
                    TracksFilterType.TAGS -> {
                        TagsFilterComponent(
                            authType = authType,
                            selectedTags = tracksFilters.tags,
                            onTagClick = onTagChange
                        )
                    }
                    TracksFilterType.CUSTOM_LISTS -> {
                        CustomListBottomSheet(
                            customLists = customLists,
                            selectedLists = tracksFilters.customLists[tracksFilters.mediaType] ?: emptyList(),
                            onListClick = onCustomListChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun <T : SortType> SortByComponent(
    config: SortConfig<T>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        AnimatedVisibility(
            visible = config.selected.type.supportsDirection,
        ) {
            SortDirectionItem(
                currentDirection = config.selected.direction,
                onDirectionToggle = { direction ->
                    if(direction != config.selected.direction) {
                        config.onSortChange(
                            Sort(
                                type = config.selected.type,
                                direction = direction
                            )
                        )
                    }
                },
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        config.options.forEach { sortType ->
            SortItem(
                sortType = sortType,
                isSelected = sortType == config.selected.type,
                onSortClick = {
                    config.onSortChange(
                        Sort(
                            type = sortType,
                            direction = config.selected.direction
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun GenresFilterComponent(
    authType: AuthType,
    selectedGenres: List<Genre>,
    onGenreClick: (Genre) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalResources.current
    val textFieldState = rememberTextFieldState()

    val genreEntries = remember(textFieldState.text) {
        Genre.entries
            .filter { authType in it.supportedBy }
            .filter { genre ->
                resources.getString(genre.displayValue())
                    .contains(textFieldState.text, ignoreCase = true)
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        modifier = modifier
    ) {
        stickyHeader {
            CustomTextField(
                textFieldState = textFieldState,
                placeholder = {
                    TextWithIcon(
                        text = stringResource(R.string.browse_search_tag_search_label),
                        iconResources = listOf(
                            IconResource.Vector(imageVector = Icons.Default.Search)
                        ),
                        placeIconAtTheBeginning = true,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(percent = 16))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        items(genreEntries) { genre ->
            CheckboxItem(
                label = stringResource(genre.displayValue()),
                isSelected = selectedGenres.contains(genre),
                onToggle = { onGenreClick(genre) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TagsFilterComponent(
    authType: AuthType,
    selectedTags: List<MediaTagEnum>,
    onTagClick: (MediaTagEnum) -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalResources.current
    val textFieldState = rememberTextFieldState()

    val tagEntries = remember(textFieldState.text) {
        MediaTagEnum.entries
            .filter { authType in it.supportedBy }
            .filter { genre ->
                resources.getString(genre.displayValue())
                    .contains(textFieldState.text, ignoreCase = true)
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        modifier = modifier
    ) {
        stickyHeader {
            CustomTextField(
                textFieldState = textFieldState,
                placeholder = {
                    TextWithIcon(
                        text = stringResource(R.string.browse_search_tag_search_label),
                        iconResources = listOf(
                            IconResource.Vector(imageVector = Icons.Default.Search)
                        ),
                        placeIconAtTheBeginning = true,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(percent = 16))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        items(tagEntries) { tag ->
            CheckboxItem(
                label = stringResource(tag.displayValue()),
                isSelected = selectedTags.contains(tag),
                onToggle = { onTagClick(tag) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CustomListBottomSheet(
    customLists: List<String>,
    selectedLists: List<String>,
    onListClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        items(customLists) { list ->
            CheckboxItem(
                label = list,
                isSelected = selectedLists.contains(list),
                onToggle = { onListClick(list) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun SortItem(
    sortType: SortType,
    isSelected: Boolean,
    onSortClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onSortClick() },
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onSortClick() }
        )
        Text(
            text = stringResource(id = sortType.displayValue()),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun SortDirectionItem(
    currentDirection: SortDirection,
    onDirectionToggle: (SortDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(all = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        SortDirection.entries.forEach { direction ->
            val isChecked = direction == currentDirection

            Text(
                text = stringResource(direction.displayValue()),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isChecked) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = if (isChecked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.background
                    )
                    .clickable {
                        onDirectionToggle(direction)
                    }
                    .padding(all = 12.dp)
            )
        }
    }
}