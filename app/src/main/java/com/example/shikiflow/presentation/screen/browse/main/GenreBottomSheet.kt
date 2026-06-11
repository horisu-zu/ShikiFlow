package com.example.shikiflow.presentation.screen.browse.main

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.Genre
import com.example.shikiflow.domain.model.media_details.MediaTagEnum
import com.example.shikiflow.domain.model.search.MediaBrowseOptions
import com.example.shikiflow.presentation.common.CheckboxItem
import com.example.shikiflow.presentation.common.ConnectedButtonGroup
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.mappers.GenreMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.TagMapper.displayValue
import com.example.shikiflow.presentation.screen.browse.main.GenreType.Companion.tabRowItem
import com.example.shikiflow.utils.IconResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GenreBottomSheet(
    authType: AuthType,
    searchOptions: MediaBrowseOptions,
    onOptionsChanged: (MediaBrowseOptions) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resources = LocalResources.current
    val sheetState = rememberBottomSheetState(
        initialValue = SheetValue.Hidden,
        enabledValues = setOf(SheetValue.Hidden, SheetValue.Expanded)
    )
    val textFieldState = rememberTextFieldState()
    val lazyGridState = rememberLazyGridState()
    var genreType by remember { mutableStateOf(GenreType.GENRE) }

    val genreEntries = remember(textFieldState.text) {
        Genre.entries
            .filter { authType in it.supportedBy }
            .filter { genre ->
                resources.getString(genre.displayValue())
                    .contains(textFieldState.text, ignoreCase = true)
            }
    }
    val tagEntries = remember(textFieldState.text) {
        MediaTagEnum.entries
            .filter { authType in it.supportedBy }
            .filter { genre ->
                resources.getString(genre.displayValue())
                    .contains(textFieldState.text, ignoreCase = true)
            }
    }

    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0 &&
            lazyGridState.firstVisibleItemScrollOffset == 0
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        dragHandle = null,
        sheetGesturesEnabled = isAtTop,
        modifier = modifier
    ) {
        (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ConnectedButtonGroup(
                    items = GenreType.entries.map { type ->
                        type.tabRowItem()
                    },
                    selectedIndex = genreType.ordinal,
                    onItemSelection = { index ->
                        genreType = GenreType.entries[index]
                    },
                    showText = true
                )
            }

            stickyHeader {
                BasicTextField(
                    state = textFieldState,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    decorator = { innerTextField ->
                        Box {
                            if(textFieldState.text.isEmpty()) {
                                TextWithIcon(
                                    text = stringResource(
                                        id = when(genreType) {
                                            GenreType.GENRE -> R.string.browse_search_genre_search_label
                                            GenreType.TAG -> R.string.browse_search_tag_search_label
                                        }
                                    ),
                                    iconResources = listOf(
                                        IconResource.Vector(imageVector = Icons.Default.Search)
                                    ),
                                    placeIconAtTheBeginning = true,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(percent = 16))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            when(genreType) {
                GenreType.GENRE -> {
                    items(count = genreEntries.size) { index ->
                        val genreItem = genreEntries[index]

                        CheckboxItem(
                            label = stringResource(id = genreItem.displayValue()),
                            isSelected = searchOptions.genres.contains(genreItem),
                            onToggle = { isSelected ->
                                onOptionsChanged(
                                    searchOptions.copy(
                                        genres = when(isSelected) {
                                            true -> searchOptions.genres - genreItem
                                            false -> searchOptions.genres + genreItem
                                        }
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
                GenreType.TAG -> {
                    items(count = tagEntries.size) { index ->
                        val tagItem = tagEntries[index]

                        CheckboxItem(
                            label = stringResource(id = tagItem.displayValue()),
                            isSelected = searchOptions.tags.contains(tagItem),
                            onToggle = { isSelected ->
                                onOptionsChanged(
                                    searchOptions.copy(
                                        tags = when(isSelected) {
                                            true -> searchOptions.tags - tagItem
                                            false -> searchOptions.tags + tagItem
                                        }
                                    )
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                        )
                    }
                }
            }
        }
    }
}