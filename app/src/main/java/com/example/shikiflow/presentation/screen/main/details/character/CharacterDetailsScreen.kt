package com.example.shikiflow.presentation.screen.main.details.character

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.data.anime.toBrowseAnime
import com.example.shikiflow.data.anime.toBrowseManga
import com.example.shikiflow.presentation.common.FormattedText
import com.example.shikiflow.presentation.screen.MainNavRoute
import com.example.shikiflow.presentation.screen.main.details.anime.CharacterCard
import com.example.shikiflow.presentation.viewmodel.character.CharacterDetailsViewModel
import com.example.shikiflow.utils.Resource

@Composable
fun CharacterDetailsScreen(
    characterId: String,
    rootNavController: NavController,
    characterDetailsViewModel: CharacterDetailsViewModel = hiltViewModel()
) {
    val isInitialized = rememberSaveable { mutableStateOf(false) }
    val characterDetailsState = characterDetailsViewModel.characterDetails.collectAsState()

    LaunchedEffect(characterId) {
        if(!isInitialized.value) {
            characterDetailsViewModel.getCharacterDetails(characterId)
            isInitialized.value = true
        }
    }

    Scaffold(
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
                    modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.Top)
                ) {
                    CharacterTitleSection(
                        avatarUrl = "${BuildConfig.BASE_URL}${characterDetails?.image?.original}",
                        name = characterDetails?.name,
                        japaneseName = characterDetails?.japanese
                    )
                    characterDetails?.description?.let { description ->
                        FormattedText(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            linkColor = MaterialTheme.colorScheme.primary,
                            brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
                            onClick = { id ->
                                Log.d("Details Screen", "Clicked id: $id")
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
                                rootNavController.navigate(MainNavRoute.AnimeDetails(id))
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
                                rootNavController.navigate(MainNavRoute.MangaDetails(id))
                            }
                        )
                    }
                }
            }
            is Resource.Error -> { /**/ }
        }
    }
}