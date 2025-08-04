package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.shikiflow.presentation.viewmodel.manga.MangaReadViewModel
import com.example.shikiflow.utils.Resource

@Composable
fun MangaChaptersScreen(
    mangaDexId: String,
    title: String,
    completedChapters: Int,
    navOptions: MangaReadNavOptions,
    mangaReadViewModel: MangaReadViewModel = hiltViewModel()
) {
    val mangaChapters = mangaReadViewModel.mangaChapters.collectAsState()

    LaunchedEffect(mangaDexId) {
        mangaReadViewModel.getMangaChapters(mangaDexId)
    }

    Scaffold { paddingValues ->
        when(mangaChapters.value) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            is Resource.Success -> {
                val sortedChapters = mangaChapters.value.data?.keys
                    ?.sortedBy { it.toFloat() } ?: emptyList()

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(sortedChapters) { chapterNumber ->
                        ChapterItem(
                            chapterNumber = chapterNumber,
                            onChapterClick = {
                                // navOptions.navigateTo
                            },
                            isCompleted = (chapterNumber.toFloatOrNull() ?: 0f) <= completedChapters,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
            is Resource.Error -> { /*TODO*/ }
        }
    }
}

@Composable
private fun ChapterItem(
    chapterNumber: String,
    onChapterClick: (String) -> Unit,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            .clickable { onChapterClick(chapterNumber) }
            .padding(horizontal = 6.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if(isCompleted) {
            Icon(
                imageVector = Icons.Default.Check,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Completed Chapter",
                modifier = Modifier.size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.75f))
                    .padding(4.dp)
            )
        }
        Text(
            text = "Chapter $chapterNumber",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}
