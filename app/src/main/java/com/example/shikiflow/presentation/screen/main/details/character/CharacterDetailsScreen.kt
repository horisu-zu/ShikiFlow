package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.comment.CommentsScreenMode
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.common.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.common.comment.CommentSection
import com.example.shikiflow.presentation.viewmodel.character.CharacterDetailsViewModel
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    characterId: Int,
    navOptions: MediaNavOptions,
    characterDetailsViewModel: CharacterDetailsViewModel = hiltViewModel()
) {
    val characterDetailsState by characterDetailsViewModel.characterDetails.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val lazyListState = rememberLazyListState()
    val isAtTop by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(characterId) {
        characterDetailsViewModel.getCharacterDetails(characterId)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = if(isAtTop) stringResource(R.string.character_title)
                                else characterDetailsState.data?.fullName ?: stringResource(R.string.character_title),
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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                            else MaterialTheme.colorScheme.surface
                    )
                )
                HorizontalDivider()
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when(characterDetailsState) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                val horizontalPadding = 12.dp

                characterDetailsState.data?.let { characterDetails ->
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize()
                            .padding(
                                top = innerPadding.calculateTopPadding(),
                                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                            ),
                        contentPadding = PaddingValues(
                            horizontal = horizontalPadding,
                            vertical = 8.dp
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
                                    descriptionHtml = description,
                                    style = MaterialTheme.typography.bodySmall,
                                    collapsedMaxLines = 3,
                                    onEntityClick = { entityType, id ->
                                        navOptions.navigateByEntity(entityType, id)
                                    }, onLinkClick = { url ->
                                        WebIntent.openUrlCustomTab(context, url)
                                    }
                                )
                            }
                        }
                        if(!characterDetails.voiceActors.isEmpty()) {
                            item {
                                LazyRow(
                                    modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding)
                                        .fillMaxWidth(),
                                    contentPadding = PaddingValues(horizontal = horizontalPadding),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(items = characterDetails.voiceActors) { seyuItem ->
                                        CharacterCard(
                                            characterPoster = seyuItem.imageUrl,
                                            characterName = seyuItem.fullName,
                                            onClick = { navOptions.navigateToStaff(seyuItem.id) },
                                            modifier = Modifier.width(96.dp)
                                        )
                                    }
                                }
                            }
                        }
                        if(!characterDetails.animeRoles.isEmpty()) {
                            item {
                                CharacterMediaSection(
                                    sectionTitle = stringResource(R.string.main_track_mode_anime),
                                    items = characterDetails.animeRoles,
                                    onItemClick = { id ->
                                        navOptions.navigateToAnimeDetails(id)
                                    },
                                    horizontalPadding = horizontalPadding
                                )
                            }
                        }
                        if(!characterDetails.mangaRoles.isEmpty()) {
                            item {
                                CharacterMediaSection(
                                    sectionTitle = stringResource(R.string.settings_manga_section_title),
                                    items = characterDetails.mangaRoles,
                                    onItemClick = { id ->
                                        navOptions.navigateToMangaDetails(id)
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
                                    }
                                )
                            }
                        }
                    }
                }
            }
            is Resource.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    ErrorItem(
                        message = characterDetailsState.message
                            ?: stringResource(R.string.common_error),
                        buttonLabel = stringResource(R.string.common_retry),
                        onButtonClick = {
                            characterDetailsViewModel.getCharacterDetails(characterId, isRefresh = true)
                        }
                    )
                }
            }
        }
    }
}