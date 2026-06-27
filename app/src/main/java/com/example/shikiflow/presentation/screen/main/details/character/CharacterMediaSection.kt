package com.example.shikiflow.presentation.screen.main.details.character

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.common.CharacterMediaRole
import com.example.shikiflow.domain.model.common.PaginatedList
import com.example.shikiflow.domain.model.common.SingleMediaRole
import com.example.shikiflow.domain.model.common.StaffMediaRole
import com.example.shikiflow.domain.model.media_details.MediaTitle.Companion.preferred
import com.example.shikiflow.domain.model.media_details.PreferredTitleType
import com.example.shikiflow.presentation.common.BrowseCoverItem
import com.example.shikiflow.presentation.common.CardFace
import com.example.shikiflow.presentation.common.FlipCard
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.TextWithDivider
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.foregroundGradient
import com.example.shikiflow.presentation.common.ignoreHorizontalParentPadding
import com.example.shikiflow.presentation.common.mappers.CharacterRoleMapper.displayValue
import com.example.shikiflow.presentation.screen.main.LocalTitleTypeController

@Composable
fun <T : SingleMediaRole> CharacterMediaSection(
    sectionTitle: String,
    items: PaginatedList<T>,
    horizontalPadding: Dp = 12.dp,
    onItemClick: (Int) -> Unit,
    onPaginatedNavigate: () -> Unit
) {
    val clip = 12.dp
    val imageType = ImageType.Poster(
        width = 120.dp,
        shape = RoundedCornerShape(clip),
    )
    val preferredTitleType = LocalTitleTypeController.current

    var cardFace by retain { mutableStateOf(CardFace.Front) }
    val cardFaceMap = retain {
        mutableStateMapOf<SingleMediaRole, CardFace>().apply {
            items.entries.forEach { role ->
                put(role, CardFace.Front)
            }
        }
    }

    val commonCardFace by remember {
        derivedStateOf {
            val firstFace = cardFaceMap.values.firstOrNull()
            val allSame = cardFaceMap.values.all { it == firstFace }

            if (allSame) firstFace else null
        }
    }

    LaunchedEffect(cardFace) {
        cardFaceMap.apply {
            items.entries.forEach { role ->
                this[role] = cardFace
            }
        }
    }

    LaunchedEffect(commonCardFace) {
        commonCardFace?.let { commonFace ->
            cardFace = commonFace
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextWithDivider(
                text = sectionTitle,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { cardFace = cardFace.next }
            ) {
                Icon(
                    painter = when (cardFace) {
                        CardFace.Front -> painterResource(R.drawable.ic_toast_outlined)
                        CardFace.Back -> painterResource(R.drawable.ic_toast_filled)
                    },
                    contentDescription = null
                )
            }

            IconButton(
                onClick = { onPaginatedNavigate() }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Navigate to Page"
                )
            }
        }

        SnapFlingLazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items.entries.size) { index ->
                MediaRoleItem(
                    mediaItem = items.entries[index],
                    imageType = imageType,
                    titleType = preferredTitleType,
                    cardFace = cardFaceMap[items.entries[index]] ?: CardFace.Front,
                    cornerShape = clip,
                    onItemClick = onItemClick,
                    onCardFaceChange = { newFace ->
                        cardFaceMap.apply {
                            this[items.entries[index]] = newFace
                        }
                    }
                )
            }
            if(items.hasNextPage) {
                item {
                    PaginatedListNavigateIcon(
                        modifier = Modifier
                            .width(imageType.width)
                            .aspectRatio(imageType.aspectRatio)
                            .clip(imageType.shape),
                        onNavigate = { onPaginatedNavigate() }
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaRoleItem(
    mediaItem: SingleMediaRole,
    imageType: ImageType,
    titleType: PreferredTitleType,
    cardFace: CardFace,
    cornerShape: Dp,
    onItemClick: (Int) -> Unit,
    onCardFaceChange: (CardFace) -> Unit,
    modifier: Modifier = Modifier
) {
    var cardFace by retain(cardFace) { mutableStateOf(cardFace) }

    LaunchedEffect(cardFace) {
        onCardFaceChange(cardFace)
    }

    FlipCard(
        cardFace = cardFace,
        modifier = modifier
            .width(imageType.width)
            .clip(imageType.shape)
            .combinedClickable(
                onClick = { onItemClick(mediaItem.shortMedia.id) },
                onLongClick = { cardFace = cardFace.next }
            ),
        front = {
            BrowseCoverItem(
                posterUrl = mediaItem.shortMedia.coverImageUrl,
                mediaType = mediaItem.shortMedia.mediaType,
                userRateStatus = mediaItem.shortMedia.userRateStatus,
                coverWidth = imageType.width,
                cornerShape = cornerShape,
                isOnTop = true,
                modifier = Modifier.foregroundGradient(
                    gradientColors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background
                    ),
                    startY = 0.6f
                )
            )

            Text(
                text = mediaItem.shortMedia.title.preferred(titleType),
                style = MaterialTheme.typography.labelMedium,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            )
        },
        back = {
            BrowseCoverItem(
                posterUrl = mediaItem.shortMedia.coverImageUrl,
                mediaType = mediaItem.shortMedia.mediaType,
                userRateStatus = null,
                coverWidth = imageType.width,
                cornerShape = cornerShape
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                when (mediaItem) {
                    is CharacterMediaRole -> {
                        mediaItem.characterRole?.let { role ->
                            Text(
                                text = stringResource(role.displayValue()),
                                style = MaterialTheme.typography.labelMedium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    is StaffMediaRole -> {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            mediaItem.staffRoles.take(4).forEach { staffRole ->
                                Text(
                                    text = staffRole,
                                    style = MaterialTheme.typography.labelSmall,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 2,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.65f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun PaginatedListNavigateIcon(
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 40.dp
) {
    Box(
        modifier = modifier
            .clickable { onNavigate() }
            .background(MaterialTheme.colorScheme.surfaceContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Navigate to Paginated Items Screen",
            modifier = Modifier.size(iconSize)
        )
    }
}