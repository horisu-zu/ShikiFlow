package com.example.shikiflow.presentation.screen.main.details

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.graphql.AnimeDetailsQuery
import com.example.shikiflow.presentation.common.CircularImage
import com.example.shikiflow.presentation.common.FormattedText
import com.example.shikiflow.presentation.common.Image

@Composable
fun AnimeDetailsDesc(
    animeDetails: AnimeDetailsQuery.Anime?,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier.fillMaxWidth()
    ) {
        val (descRef, genresRef, charactersRef) = createRefs()

        FormattedText(
            text = animeDetails?.description ?: "No Description",
            modifier = Modifier.constrainAs(descRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            style = MaterialTheme.typography.bodySmall,
            linkColor = MaterialTheme.colorScheme.primary,
            brushColor = MaterialTheme.colorScheme.background.copy(0.8f),
            onClick = { id ->
                Log.d("Details Screen", "Clicked id: $id")
            }
        )

        LazyRow(
            modifier = Modifier.constrainAs(genresRef) {
                top.linkTo(descRef.bottom, margin = 2.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(animeDetails?.genres ?: emptyList()) { genreItem ->
                GenreCard(genreItem.name)
            }
        }

        Column(
            modifier = Modifier.constrainAs(charactersRef) {
                top.linkTo(genresRef.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Characters",
                style = MaterialTheme.typography.titleMedium
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(animeDetails?.characterRoles ?: emptyList()) { characterItem ->
                    CharacterCard(
                        character = characterItem.character,
                        onClick = { /*TODO*/ }
                    )
                }
            }
        }
    }
}

@Composable
fun GenreCard(
    genre: String
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = { /*TODO*/ }
    ) {
        Text(
            text = genre,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun CharacterCard(
    character: AnimeDetailsQuery.Character,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(96.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        CircularImage(
            model = character.characterShort.poster?.posterShort?.previewUrl,
            size = 96.dp,
            contentScale = ContentScale.Crop
        )
        Text(
            text = character.characterShort.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp)
        )
    }
}