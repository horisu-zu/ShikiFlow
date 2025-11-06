package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.toBrowseAnime
import com.example.shikiflow.domain.model.anime.toBrowseManga
import com.example.shikiflow.domain.model.common.ShikiImage
import com.example.shikiflow.domain.model.favorite.FavoriteCategory
import com.example.shikiflow.domain.model.favorite.ShikiFavorite
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.details.anime.CharacterCard
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
fun FavoritesSection(
    favoritesMap: Map<FavoriteCategory, List<ShikiFavorite>>,
    modifier: Modifier = Modifier
) {
    var currentSection by remember { mutableStateOf(favoritesMap.keys.first()) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .clip(RoundedCornerShape(
                    topEnd = 8.dp, topStart = 8.dp,
                    bottomEnd = 4.dp, bottomStart = 4.dp
                ))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 4.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                favoritesMap.keys.forEachIndexed { index, type ->
                    ToggleButton(
                        checked = currentSection == type,
                        onCheckedChange = { currentSection = type },
                        shapes = when {
                            favoritesMap.size == 1 -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                            index == 0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            index == favoritesMap.size - 1 -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                        contentPadding = ButtonDefaults.ExtraSmallContentPadding,
                        colors = ToggleButtonDefaults.toggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            checkedContainerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            checkedContentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        type.iconRes.toIcon(modifier = Modifier.size(24.dp))
                    }
                }
            }
            Text(
                text = buildString {
                    append(stringResource(R.string.favorite_prefix))
                    append(" ")
                    append(stringResource(currentSection.titleResId))
                },
                style = MaterialTheme.typography.titleMedium
            )
        }
        AnimatedContent(
            targetState = currentSection,
            transitionSpec = {
                fadeIn() + slideInVertically() togetherWith fadeOut() + slideOutVertically()
            }
        ) { currentSection ->
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
            ) {
                favoritesMap[currentSection]?.forEach { item ->
                    when(item) {
                        is ShikiFavorite.FavoriteAnime -> {
                            FavoriteMediaItem(
                                browseItem = item.shikiAnime.copy(
                                    image = ShikiImage(original = item.shikiAnime.image?.original?.replace("/x64/", "/original/"))
                                ).toBrowseAnime(),
                                modifier = Modifier.width(108.dp)
                            )
                        }
                        is ShikiFavorite.FavoriteManga -> {
                            FavoriteMediaItem(
                                browseItem = item.shikiManga.copy(
                                    image = ShikiImage(original = item.shikiManga.image?.original?.replace("/x64/", "/original/"))
                                ).toBrowseManga(),
                                modifier = Modifier.width(108.dp)
                            )
                        }
                        is ShikiFavorite.FavoriteCharacter -> {
                            CharacterCard(
                                characterPoster = "${BuildConfig.BASE_URL}${item.shikiCharacter.image.original?.replace("/x64/", "/original/")}",
                                characterName = item.shikiCharacter.name,
                                onClick = { /**/ },
                                modifier = Modifier.width(108.dp)
                            )
                        }
                        is ShikiFavorite.FavoritePerson -> {
                            CharacterCard(
                                characterPoster = "${BuildConfig.BASE_URL}${item.shikiPerson.image.original?.replace("/x64/", "/original/")}",
                                characterName = item.shikiPerson.name,
                                onClick = { /**/ },
                                modifier = Modifier.width(108.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteMediaItem(
    browseItem: Browse,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        BaseImage(
            model = browseItem.posterUrl,
            contentScale = ContentScale.Crop,
            imageType = ImageType.Poster(
                defaultWidth = Int.MAX_VALUE.dp,
            )
        )
        Text(
            text = browseItem.title,
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2
        )
    }
}