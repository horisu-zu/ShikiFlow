package com.example.shikiflow.presentation.screen.main.details.roles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.CharacterMediaRole
import com.example.shikiflow.domain.model.common.MediaRole
import com.example.shikiflow.presentation.screen.main.details.RoleType
import com.example.shikiflow.domain.model.common.StaffMediaRole
import com.example.shikiflow.domain.model.common.VoiceActorMediaRole
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.BrowseCoverItem
import com.example.shikiflow.presentation.common.ErrorItem
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.mappers.CharacterRoleMapper.displayValue
import com.example.shikiflow.presentation.screen.main.details.MediaNavOptions

@Composable
fun MediaRolesContent(
    roleType: RoleType,
    mediaRoles: LazyPagingItems<MediaRole>,
    navOptions: MediaNavOptions,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier
) {
    when (mediaRoles.loadState.refresh) {
        is LoadState.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        }
        is LoadState.Error -> {
            val errorMessage = (mediaRoles.loadState.refresh as LoadState.Error)
                .error.message

            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                ErrorItem(
                    message = errorMessage ?: stringResource(id = R.string.common_error),
                    buttonLabel = stringResource(id = R.string.common_retry),
                    onButtonClick = { mediaRoles.retry() }
                )
            }
        }
        else -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(
                    minSize = when(roleType) {
                        RoleType.VA -> 480.dp
                        else -> 240.dp
                    }
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding()),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 8.dp,
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                ),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
            ) {
                items(count = mediaRoles.itemCount) { index ->
                    mediaRoles[index]?.let { mediaRole ->
                        when(mediaRole) {
                            is CharacterMediaRole -> {
                                CharacterMediaRoleItem(
                                    mediaRole = mediaRole,
                                    onMediaClick = { id, mediaType ->
                                        when (mediaType) {
                                            MediaType.ANIME -> navOptions.navigateToAnimeDetails(id)
                                            MediaType.MANGA -> navOptions.navigateToMangaDetails(id)
                                        }
                                    }
                                )
                            }
                            is StaffMediaRole -> {
                                StaffMediaRoleItem(
                                    mediaRole = mediaRole,
                                    onMediaClick = { id, mediaType ->
                                        when (mediaType) {
                                            MediaType.ANIME -> navOptions.navigateToAnimeDetails(id)
                                            MediaType.MANGA -> navOptions.navigateToMangaDetails(id)
                                        }
                                    }
                                )
                            }
                            is VoiceActorMediaRole -> {
                                VoiceActorMediaRoleItem(
                                    vaRole = mediaRole,
                                    onCharacterClick = { id ->
                                        navOptions.navigateToCharacterDetails(id)
                                    },
                                    onMediaClick = { id, mediaType ->
                                        when (mediaType) {
                                            MediaType.ANIME -> navOptions.navigateToAnimeDetails(id)
                                            MediaType.MANGA -> navOptions.navigateToMangaDetails(id)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                mediaRoles.apply {
                    if (loadState.append is LoadState.Loading) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) { CircularProgressIndicator() }
                        }
                    }
                    if (loadState.append is LoadState.Error) {
                        item(span = { GridItemSpan(maxLineSpan) }) {
                            ErrorItem(
                                message = stringResource(R.string.common_error),
                                showFace = false,
                                buttonLabel = stringResource(R.string.common_retry),
                                onButtonClick = { mediaRoles.retry() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CharacterMediaRoleItem(
    mediaRole: CharacterMediaRole,
    onMediaClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerShape = 12.dp

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onMediaClick(mediaRole.shortMedia.id, mediaRole.shortMedia.mediaType) },
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
    ) {
        BrowseCoverItem(
            posterUrl = mediaRole.shortMedia.coverImageUrl,
            mediaType = mediaRole.shortMedia.mediaType,
            userRateStatus = mediaRole.shortMedia.userRateStatus,
            coverWidth = 96.dp,
            cornerShape = cornerShape
        )
        Column(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Text(
                text = mediaRole.shortMedia.title,
                style = MaterialTheme.typography.labelMedium
            )
            mediaRole.characterRole?.let { role ->
                Text(
                    text = stringResource(id = role.displayValue()),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.8f
                        )
                    )
                )
            }
        }
    }
}

@Composable
private fun StaffMediaRoleItem(
    mediaRole: StaffMediaRole,
    onMediaClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerShape = 12.dp

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onMediaClick(mediaRole.shortMedia.id, mediaRole.shortMedia.mediaType) },
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
    ) {
        BrowseCoverItem(
            posterUrl = mediaRole.shortMedia.coverImageUrl,
            mediaType = mediaRole.shortMedia.mediaType,
            userRateStatus = mediaRole.shortMedia.userRateStatus,
            coverWidth = 96.dp,
            cornerShape = cornerShape
        )
        Column(
            modifier = Modifier.padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Text(
                text = mediaRole.shortMedia.title,
                style = MaterialTheme.typography.labelMedium
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top)
            ) {
                mediaRole.staffRoles.forEach { staffRole ->
                    Text(
                        text = staffRole,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground.copy(
                                alpha = 0.8f
                            )
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun VoiceActorMediaRoleItem(
    vaRole: VoiceActorMediaRole,
    onCharacterClick: (Int) -> Unit,
    onMediaClick: (Int, MediaType) -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerShape = 12.dp
    val imageWidth = 96.dp

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.clip(
                shape = RoundedCornerShape(
                    topEnd = cornerShape,
                    topStart = cornerShape,
                    bottomStart = 4.dp,
                    bottomEnd = 4.dp
                )
            )
                .clickable { onCharacterClick(vaRole.characterShort.id) },
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BaseImage(
                model = vaRole.characterShort.imageUrl,
                imageType = ImageType.Poster(
                    width = imageWidth,
                    clip = RoundedCornerShape(cornerShape)
                )
            )
            Text(
                text = vaRole.characterShort.fullName,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.width(imageWidth - 6.dp)
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            vaRole.shortMediaList.forEach { shortMedia ->
                Column(
                    modifier = Modifier.clip(
                        shape = RoundedCornerShape(
                            topEnd = cornerShape,
                            topStart = cornerShape,
                            bottomStart = 4.dp,
                            bottomEnd = 4.dp
                        )
                    ).clickable { onMediaClick(shortMedia.id, shortMedia.mediaType) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
                ) {
                    BrowseCoverItem(
                        posterUrl = shortMedia.coverImageUrl,
                        mediaType = shortMedia.mediaType,
                        userRateStatus = shortMedia.userRateStatus,
                        coverWidth = imageWidth,
                        cornerShape = cornerShape
                    )
                    Text(
                        text = shortMedia.title,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(imageWidth - 6.dp)
                    )
                }
            }
        }
    }
}