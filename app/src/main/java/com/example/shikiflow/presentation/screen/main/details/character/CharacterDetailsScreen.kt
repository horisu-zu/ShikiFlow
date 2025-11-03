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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.toBrowseAnime
import com.example.shikiflow.domain.model.anime.toBrowseManga
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.anime.CharacterCard
import com.example.shikiflow.presentation.screen.main.details.common.CommentSection
import com.example.shikiflow.presentation.screen.main.details.common.CommentsScreenMode
import com.example.shikiflow.presentation.viewmodel.character.CharacterDetailsViewModel
import com.example.shikiflow.utils.Resource
import com.example.shikiflow.utils.WebIntent
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    characterId: String,
    navOptions: MediaNavOptions,
    characterDetailsViewModel: CharacterDetailsViewModel = hiltViewModel()
) {
    val characterDetailsState by characterDetailsViewModel.characterDetails.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val scrollState = rememberScrollState()
    val isAtTop by remember {
        derivedStateOf {
            scrollState.value <= 0
        }
    }

    LaunchedEffect(characterId) {
        characterDetailsViewModel.getCharacterDetails(characterId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if(isAtTop) stringResource(R.string.character_title)
                            else characterDetailsState.data?.name ?: stringResource(R.string.character_title),
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
                    Column(
                        modifier = Modifier.fillMaxSize().padding(
                            top = innerPadding.calculateTopPadding(),
                            start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                            end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                        ).padding(horizontal = horizontalPadding)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                    ) {
                        CharacterTitleSection(
                            avatarUrl = "${BuildConfig.BASE_URL}${characterDetails.image.original}",
                            name = characterDetails.name,
                            japaneseName = characterDetails.japanese
                        )
                        characterDetails.descriptionHtml?.let { description ->
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
                        if(!characterDetails.seyu.isNullOrEmpty()) {
                            LazyRow(
                                modifier = Modifier.ignoreHorizontalParentPadding(horizontalPadding)
                                    .fillMaxWidth(),
                                contentPadding = PaddingValues(horizontal = horizontalPadding),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(items = characterDetails.seyu) { seyuItem ->
                                    CharacterCard(
                                        characterPoster = "${BuildConfig.BASE_URL}${seyuItem.image?.original}",
                                        characterName = seyuItem.name,
                                        onClick = { navOptions.navigateToPerson(seyuItem.id.toString()) }
                                    )
                                }
                            }
                        }
                        if(!characterDetails.animes.isNullOrEmpty()) {
                            val browseItems = characterDetails.animes.map { item ->
                                item.toBrowseAnime()
                            }
                            CharacterMediaSection(
                                sectionTitle = stringResource(R.string.main_track_mode_anime),
                                items = browseItems,
                                onItemClick = { id, mediaType ->
                                    navOptions.navigateToAnimeDetails(id)
                                }, horizontalPadding = horizontalPadding
                            )
                        }
                        if(!characterDetails.mangas.isNullOrEmpty()) {
                            val browseItems = characterDetails.mangas.map { item ->
                                item.toBrowseManga()
                            }
                            CharacterMediaSection(
                                sectionTitle = stringResource(R.string.settings_manga_section_title),
                                items = browseItems,
                                onItemClick = { id, mediaType ->
                                    navOptions.navigateToMangaDetails(id)
                                }, horizontalPadding = horizontalPadding
                            )
                        }
                        characterDetails.topicId?.let { topicId ->
                            CommentSection(
                                topicId = topicId.toString(),
                                isRefreshing = false,
                                onTopicNavigate = {
                                    navOptions.navigateToComments(
                                        screenMode = CommentsScreenMode.TOPIC,
                                        id = topicId.toString()
                                    )
                                },
                                onLinkClick = { url ->
                                    WebIntent.openUrlCustomTab(context, url)
                                },
                                onEntityClick = { entityType, id ->
                                    navOptions.navigateByEntity(entityType, id)
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
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