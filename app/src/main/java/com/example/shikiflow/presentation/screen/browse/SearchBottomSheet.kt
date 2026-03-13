package com.example.shikiflow.presentation.screen.browse

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.media_details.AgeRating
import com.example.shikiflow.domain.model.search.BrowseOptions
import com.example.shikiflow.domain.model.media_details.MediaStatus
import com.example.shikiflow.domain.model.sort.MediaSort
import com.example.shikiflow.domain.model.track.MediaFormat
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ChipSection
import com.example.shikiflow.presentation.common.mappers.AgeRatingMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaFormatMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaStatusMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.displayValue
import com.example.shikiflow.presentation.common.mappers.SortMapper.displayValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    authType: AuthType,
    searchOptions: BrowseOptions,
    onTypeChanged: (MediaType) -> Unit,
    onOptionsChanged: (BrowseOptions) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            ChipSection(
                label = stringResource(R.string.browse_search_label_media),
                items = MediaType.entries,
                selectedItem = searchOptions.mediaType,
                itemLabel = { stringResource(it.displayValue()) },
                onItemSelected = { newType ->
                    onTypeChanged(newType)
                }
            )
            ChipSection(
                label = stringResource(R.string.browse_search_label_kind),
                items = MediaFormat.entries.filter { formatEntry ->
                    formatEntry.mediaType == searchOptions.mediaType
                }.filter { formatEntry ->
                    authType in formatEntry.supportedBy
                },
                selectedItem = searchOptions.format,
                itemLabel = { stringResource(it.displayValue()) },
                onItemSelected = { newFormat ->
                    onOptionsChanged(
                        searchOptions.copy(
                            format = if(searchOptions.format == newFormat) null else newFormat
                        )
                    )
                }
            )
            ChipSection(
                label = stringResource(R.string.browse_search_label_status),
                items = MediaStatus.entries.filter { statusEntry ->
                    authType to searchOptions.mediaType !in statusEntry.exclusions
                }.filter { status ->
                    searchOptions.mediaType in status.mediaType
                },
                selectedItem = searchOptions.status,
                itemLabel = { stringResource(it.displayValue()) },
                onItemSelected = { newStatus ->
                    onOptionsChanged(
                        searchOptions.copy(
                            status = if(searchOptions.status == newStatus) null else newStatus
                        )
                    )
                }
            )
            ChipSection(
                label = stringResource(R.string.browse_search_label_sort_by),
                items = when(authType) {
                    AuthType.SHIKIMORI -> MediaSort.Shikimori.entries
                    AuthType.ANILIST -> MediaSort.Anilist.entries
                },
                selectedItem = searchOptions.order,
                itemLabel = { stringResource(it.displayValue()) },
                onItemSelected = { newOrder ->
                    onOptionsChanged(
                        searchOptions.copy(
                            order = if(searchOptions.order == newOrder) null else newOrder
                        )
                    )
                }
            )
            if(authType == AuthType.SHIKIMORI && searchOptions.mediaType == MediaType.ANIME) {
                ChipSection(
                    label = stringResource(R.string.browse_search_label_age_rating),
                    items = AgeRating.entries,
                    selectedItem = searchOptions.ageRating,
                    itemLabel = { stringResource(it.displayValue()) },
                    onItemSelected = { newRating ->
                        onOptionsChanged(
                            searchOptions.copy(
                                ageRating = if(searchOptions.ageRating == newRating) null else newRating
                            )
                        )
                    }
                )
            }
        }
    }
}
