package com.example.shikiflow.presentation.screen.more.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.BuildConfig
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.Browse
import com.example.shikiflow.domain.model.anime.toBrowseAnime
import com.example.shikiflow.domain.model.anime.toBrowseManga
import com.example.shikiflow.domain.model.common.ShikiImage
import com.example.shikiflow.domain.model.favorite.ShikiFavorite
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.screen.main.details.anime.CharacterCard
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun FavoritesSection(
    userFavoritesData: List<ShikiFavorite>,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    val favoritesMap = userFavoritesData.groupBy { favorite ->
        when(favorite) {
            is ShikiFavorite.FavoriteAnime -> stringResource(R.string.main_track_mode_anime)
            is ShikiFavorite.FavoriteManga -> stringResource(R.string.main_track_mode_manga)
            is ShikiFavorite.FavoriteCharacter -> stringResource(R.string.details_characters)
            is ShikiFavorite.FavoritePerson -> stringResource(favorite.personType.resId)
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.profile_screen_favorites),
            style = MaterialTheme.typography.headlineSmall
        )
        favoritesMap.forEach { favoriteType ->
            FavoriteSectionItem(
                label = favoriteType.key,
                favoriteItemList = favoriteType.value,
                horizontalPadding = horizontalPadding
            )
        }
    }
}

@Composable
private fun FavoriteSectionItem(
    label: String,
    favoriteItemList: List<ShikiFavorite>,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.Top)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium
        )
        LazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            items(favoriteItemList) { item ->
                when(item) {
                    is ShikiFavorite.FavoriteAnime -> {
                        FavoriteMediaItem(
                            browseItem = item.shikiAnime.copy(
                                image = ShikiImage(original = item.shikiAnime.image?.original?.replace("/x64/", "/original/"))
                            ).toBrowseAnime(),
                            modifier = Modifier.width(120.dp)
                        )
                    }
                    is ShikiFavorite.FavoriteManga -> {
                        FavoriteMediaItem(
                            browseItem = item.shikiManga.copy(
                                image = ShikiImage(original = item.shikiManga.image?.original?.replace("/x64/", "/original/"))
                            ).toBrowseManga(),
                            modifier = Modifier.width(120.dp)
                        )
                    }
                    is ShikiFavorite.FavoriteCharacter -> {
                        CharacterCard(
                            characterPoster = "${BuildConfig.BASE_URL}${item.shikiCharacter.image.original?.replace("/x64/", "/original/")}",
                            characterName = item.shikiCharacter.name,
                            onClick = { /**/ }
                        )
                    }
                    is ShikiFavorite.FavoritePerson -> {
                        CharacterCard(
                            characterPoster = "${BuildConfig.BASE_URL}${item.shikiPerson.image.original?.replace("/x64/", "/original/")}",
                            characterName = item.shikiPerson.name,
                            onClick = { /**/ }
                        )
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