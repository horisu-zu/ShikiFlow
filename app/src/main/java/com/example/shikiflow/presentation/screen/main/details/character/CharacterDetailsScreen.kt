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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.presentation.screen.main.details.MediaRolesType
import com.example.shikiflow.presentation.screen.main.details.RoleType
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.common.ToggleFavoriteButton
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.common.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentSection
import com.example.shikiflow.presentation.viewmodel.character.details.CharacterDetailsViewModel
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    characterId: Int,
    navOptions: MediaNavOptions,
    characterDetailsViewModel: CharacterDetailsViewModel = hiltViewModel()
) {
    val uiState by characterDetailsViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
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
                            text = if(isAtTop) stringResource(R.string.character_title)
                                else uiState.details?.fullName ?: stringResource(R.string.character_title),
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
            val horizontalPadding = 12.dp

            uiState.details?.let { characterDetails ->
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = innerPadding.calculateTopPadding()),
                    contentPadding = PaddingValues(
                        start = horizontalPadding,
                        end = horizontalPadding,
                        top = 8.dp,
                        bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
                ) {
                    item {
                        CharacterTitleSection(
                            avatarUrl = characterDetails.imageUrl,
                            name = characterDetails.fullName,
                            japaneseName = characterDetails.nativeName
                        )
                    }
                    characterDetails.description?.let { description ->
                        item {
                            ExpandableText(
                                htmlText = description,
                                authType = uiState.authType,
                                style = MaterialTheme.typography.bodySmall,
                                collapsedMaxLines = 3,
                                onEntityClick = { entityType, id ->
                                    navOptions.navigateByEntity(entityType, id)
                                },
                                onLinkClick = { url ->
                                    WebIntent.openUrlCustomTab(context, url)
                                }
                            )
                        }
                    }
                    if(!characterDetails.voiceActors.isEmpty()) {
                        item {
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
                                        characterName = vaItem.fullName,
                                        onClick = { navOptions.navigateToStaff(vaItem.id) },
                                        modifier = Modifier.width(96.dp)
                                    )
                                }
                            }
                        }
                    }
                    if(characterDetails.animeRoles.entries.isNotEmpty()) {
                        item {
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
                        item {
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
                        item {
                            CommentSection(
                                topicId = topicId,
                                onTopicNavigate = {
                                    navOptions.navigateToComments(
                                        screenMode = CommentsScreenMode.TOPIC,
                                        id = topicId
                                    )
                                },
                                onLinkClick = { url ->
                                    WebIntent.openUrlCustomTab(context, url)
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