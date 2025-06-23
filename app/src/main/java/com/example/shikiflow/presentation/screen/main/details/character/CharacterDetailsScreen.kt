package com.example.shikiflow.presentation.screen.main.details.character

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.anime.toBrowseAnime
import com.example.shikiflow.data.anime.toBrowseManga
import com.example.shikiflow.presentation.common.ExpandableText
import com.example.shikiflow.presentation.screen.MediaNavOptions
import com.example.shikiflow.presentation.screen.main.details.anime.CharacterCard
import com.example.shikiflow.presentation.viewmodel.character.CharacterDetailsViewModel
import com.example.shikiflow.utils.Converter.EntityType
import com.example.shikiflow.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailsScreen(
    characterId: String,
    navOptions: MediaNavOptions,
    characterDetailsViewModel: CharacterDetailsViewModel = hiltViewModel()
) {
    val isInitialized = rememberSaveable { mutableStateOf(false) }
    val characterDetailsState = characterDetailsViewModel.characterDetails.collectAsState()
    val customTabIntent = CustomTabsIntent.Builder().build()
    val context = LocalContext.current

    val scrollState = rememberScrollState()
    val isAtTop by remember {
        derivedStateOf {
            scrollState.value <= 0
        }
    }

    LaunchedEffect(characterId) {
        if(!isInitialized.value) {
            characterDetailsViewModel.getCharacterDetails(characterId)
            isInitialized.value = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if(isAtTop) "Character"
                            else characterDetailsState.value.data?.name ?: "Character",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navOptions.navigateBack() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Main"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if(isAtTop) MaterialTheme.colorScheme.background
                        else MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        when(characterDetailsState.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Success -> {
                val characterDetails = characterDetailsState.value.data

                Column(
                    modifier = Modifier.fillMaxSize().padding(
                        top = innerPadding.calculateTopPadding(),
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                    ).verticalScroll(scrollState).padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                ) {
                    CharacterTitleSection(
                        avatarUrl = "${BuildConfig.BASE_URL}${characterDetails?.image?.original}",
                        name = characterDetails?.name,
                        japaneseName = characterDetails?.japanese
                    )
                    characterDetails?.descriptionHtml?.let { description ->
                        ExpandableText(
                            descriptionHtml = description,
                            style = MaterialTheme.typography.bodySmall,
                            collapsedMaxLines = 3,
                            onEntityClick = { entityType, id ->
                                when(entityType) {
                                    EntityType.CHARACTER -> {
                                        navOptions.navigateToCharacterDetails(id)
                                    }
                                    EntityType.PERSON -> {
                                        customTabIntent.launchUrl(context,
                                            "${BuildConfig.BASE_URL}/person/$id".toUri()
                                        )
                                    }
                                    EntityType.ANIME -> {
                                        navOptions.navigateToAnimeDetails(id)
                                    }
                                    EntityType.MANGA -> {
                                        navOptions.navigateToMangaDetails(id)
                                    }
                                }
                            }
                        )
                    }
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(characterDetails?.seyu ?: emptyList()) { seyuItem ->
                            CharacterCard(
                                characterPoster = "${BuildConfig.BASE_URL}${seyuItem.image?.original}",
                                characterName = seyuItem.name ?: "Unknown",
                                onClick = { /**/ }
                            )
                        }
                    }
                    if(!characterDetails?.animes.isNullOrEmpty()) {
                        val browseItems = characterDetails.animes.map { item ->
                            item.toBrowseAnime()
                        }
                        CharacterMediaSection(
                            sectionTitle = "Anime",
                            items = browseItems,
                            onItemClick = { id, mediaType ->
                                navOptions.navigateToAnimeDetails(id)
                            }
                        )
                    }
                    if(!characterDetails?.mangas.isNullOrEmpty()) {
                        val browseItems = characterDetails.mangas.map { item ->
                            item.toBrowseManga()
                        }
                        CharacterMediaSection(
                            sectionTitle = "Manga",
                            items = browseItems,
                            onItemClick = { id, mediaType ->
                                navOptions.navigateToMangaDetails(id)
                            }
                        )
                    }
                }
            }
            is Resource.Error -> { /**/ }
        }
    }
}