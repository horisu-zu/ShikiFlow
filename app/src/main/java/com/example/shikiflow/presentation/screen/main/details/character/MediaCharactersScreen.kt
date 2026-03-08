package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.viewmodel.character.MediaCharactersViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaCharactersScreen(
    mediaId: Int,
    mediaTitle: String,
    mediaType: MediaType,
    navOptions: MediaNavOptions,
    mediaCharactersViewModel: MediaCharactersViewModel = hiltViewModel()
) {
    val mediaCharacterItems = mediaCharactersViewModel.getMediaCharacters(
        mediaId, mediaType
    ).collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                        ) {
                            Text(
                                text = mediaTitle,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = stringResource(R.string.details_characters),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navOptions.navigateBack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    ) { paddingValues ->
        when (mediaCharacterItems.loadState.refresh) {
            is LoadState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is LoadState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = { mediaCharacterItems.refresh() }
                    )
                }
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(360.dp),
                    contentPadding = PaddingValues(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    ),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            top = paddingValues.calculateTopPadding(),
                            start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                            end = paddingValues.calculateEndPadding(LayoutDirection.Ltr)
                        ),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
                ) {
                    items(
                        count = mediaCharacterItems.itemCount,
                        key = mediaCharacterItems.itemKey { it.mediaCharacter.id }
                    ) { index ->
                        val mediaCharacterShort = mediaCharacterItems[index] ?: return@items

                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MediaCharacterItem(
                                mediaPerson = mediaCharacterShort.mediaCharacter,
                                role = stringResource(id = mediaCharacterShort.role.displayValue),
                                leftToRight = true,
                                onItemClick = { characterId ->
                                    navOptions.navigateToCharacterDetails(characterId)
                                },
                                modifier = Modifier.weight(1f)
                            )
                            mediaCharacterShort.mediaPerson?.let { va ->
                                MediaCharacterItem(
                                    mediaPerson = va,
                                    leftToRight = false,
                                    onItemClick = { personId ->
                                        navOptions.navigateToStaff(personId)
                                    },
                                    modifier = Modifier.weight(1f)
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
private fun MediaCharacterItem(
    mediaPerson: MediaPersonShort,
    role: String? = null,
    leftToRight: Boolean,
    onItemClick: (Int) -> Unit,
    modifier: Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onItemClick(mediaPerson.id) },
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if(leftToRight) {
            BaseImage(
                model = mediaPerson.imageUrl,
                modifier = Modifier.width(96.dp)
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            horizontalAlignment = if(leftToRight) Alignment.Start else Alignment.End
        ) {
            Text(
                text = mediaPerson.fullName,
                style = MaterialTheme.typography.labelMedium.copy(
                    textAlign = if(leftToRight) TextAlign.Start else TextAlign.End
                )
            )
            role?.let {
                Text(
                    text = role,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        ),
                        textAlign = if(leftToRight) TextAlign.Start else TextAlign.End
                    )
                )
            }
        }
        if(!leftToRight) {
            BaseImage(
                model = mediaPerson.imageUrl,
                modifier = Modifier.width(96.dp)
            )
        }
    }
}