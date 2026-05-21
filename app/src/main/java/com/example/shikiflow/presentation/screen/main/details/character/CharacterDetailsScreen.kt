package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.domain.model.staff.StaffName.Companion.preferred
import com.example.shikiflow.presentation.screen.main.details.MediaRolesType
import com.example.shikiflow.presentation.screen.main.details.RoleType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.RichTextRenderer
import com.example.shikiflow.presentation.common.ToggleFavoriteButton
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.common.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentSection
import com.example.shikiflow.presentation.viewmodel.character.details.CharacterDetailsViewModel
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController
import com.example.shikiflow.presentation.screen.main.details.staff.StaffAttributes
import com.example.shikiflow.utils.Converter.isHTMLStringBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    characterId: Int,
    navOptions: MediaNavOptions,
    characterDetailsViewModel: CharacterDetailsViewModel = hiltViewModel()
) {
    val uiState by characterDetailsViewModel.uiState.collectAsStateWithLifecycle()
    val titleType = LocalTitleTypeController.current

    val lazyGridState = rememberLazyGridState()
    val isAtTop by remember {
        derivedStateOf {
            lazyGridState.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(characterId) {
        characterDetailsViewModel.setCharacterId(characterId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = if(isAtTop) {
                                stringResource(R.string.character_title)
                            } else uiState.details?.fullName?.preferred(titleType)
                                ?: stringResource(R.string.character_title),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navOptions.navigateBack() }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Main"
                            )
                        }
                    },
                    actions = {
                        uiState.details?.let { details ->
                            if(details.favorites != null && details.isFavorite != null) {
                                ToggleFavoriteButton(
                                    favoritesCount = details.favorites,
                                    isFavorite = details.isFavorite,
                                    onToggle = { characterDetailsViewModel.toggleFavorite(details.id) }
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                            else MaterialTheme.colorScheme.surfaceContainer
                    )
                )
                HorizontalDivider()
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if(uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if(uiState.errorMessage != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = uiState.errorMessage
                        ?: stringResource(R.string.common_error),
                    buttonLabel = stringResource(R.string.common_retry),
                    onButtonClick = { characterDetailsViewModel.onRefresh() }
                )
            }
        } else {
            uiState.details?.let { characterDetails ->
                val horizontalPadding = 12.dp

                LazyVerticalGrid(
                    state = lazyGridState,
                    columns = GridCells.Adaptive(180.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding()),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 8.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        CharacterTitleSection(
                            avatarUrl = characterDetails.imageUrl,
                            name = characterDetails.fullName.preferred(titleType),
                            nativeName = characterDetails.fullName.native
                        )
                    }
                    characterDetails.attributes?.let { attributes ->
                        StaffAttributes(attributes)
                    }
                    if(!characterDetails.description.isHTMLStringBlank()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            RichTextRenderer(
                                htmlText = characterDetails.description ?: "",
                                style = MaterialTheme.typography.bodySmall,
                                onEntityClick = { entityType, id ->
                                    navOptions.navigateByEntity(entityType, id)
                                }
                            )
                        }
                    }
                    if(!characterDetails.voiceActors.isEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Column(
                                modifier = Modifier.padding(top = 4.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                            ) {
                                TextWithDivider(
                                    text = stringResource(R.string.user_stats_section_voice_actors),
                                    style = MaterialTheme.typography.titleMedium
                                )

                                SnapFlingLazyRow(
                                    modifier = Modifier
                                        .ignoreHorizontalParentPadding(horizontalPadding)
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(items = characterDetails.voiceActors) { vaItem ->
                                        CharacterCard(
                                            characterPoster = vaItem.imageUrl,
                                            characterName = vaItem.fullName.preferred(titleType),
                                            onClick = { navOptions.navigateToStaff(vaItem.id) },
                                            modifier = Modifier.width(96.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if(characterDetails.animeRoles.entries.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            CharacterMediaSection(
                                sectionTitle = stringResource(R.string.media_type_anime),
                                items = characterDetails.animeRoles,
                                onItemClick = { id ->
                                    navOptions.navigateToAnimeDetails(id)
                                },
                                onPaginatedNavigate = {
                                    navOptions.navigateToMediaRoles(
                                        id = characterId,
                                        mediaRolesType = MediaRolesType.CHARACTER,
                                        roleTypes = buildList {
                                            add(RoleType.ANIME)
                                            if(characterDetails.mangaRoles.entries.isNotEmpty()) {
                                                add(RoleType.MANGA)
                                            }
                                        }
                                    )
                                },
                                horizontalPadding = horizontalPadding
                            )
                        }
                    }
                    if(characterDetails.mangaRoles.entries.isNotEmpty()) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            CharacterMediaSection(
                                sectionTitle = stringResource(R.string.media_type_manga),
                                items = characterDetails.mangaRoles,
                                onItemClick = { id ->
                                    navOptions.navigateToMangaDetails(id)
                                },
                                onPaginatedNavigate = {
                                    navOptions.navigateToMediaRoles(
                                        id = characterId,
                                        mediaRolesType = MediaRolesType.CHARACTER,
                                        roleTypes = buildList {
                                            add(RoleType.MANGA)
                                            if(characterDetails.animeRoles.entries.isNotEmpty()) {
                                                add(RoleType.ANIME)
                                            }
                                        }
                                    )
                                },
                                horizontalPadding = horizontalPadding
                            )
                        }
                    }
                    characterDetails.topicId?.let { topicId ->
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            CommentSection(
                                topicId = topicId,
                                onTopicNavigate = {
                                    navOptions.navigateToComments(
                                        screenMode = CommentsScreenMode.TOPIC,
                                        id = topicId
                                    )
                                },
                                onEntityClick = { entityType, id ->
                                    navOptions.navigateByEntity(entityType, id)
                                },
                                onUserClick = { user ->
                                    navOptions.navigateToUserProfile(user)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}