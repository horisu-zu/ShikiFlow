package com.example.shikiflow.presentation.screen.browse

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.graphql.AnimeBrowseQuery
import com.example.shikiflow.data.mapper.UserRateMapper
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun BrowseItem(
    anime: AnimeBrowseQuery.Anime,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = { onItemClick(anime.id) }
            )
    ) {
        val (posterRef, titleRef, infoRef) = createRefs()

        BaseImage(
            model = anime.poster?.posterShort?.mainUrl,
            contentScale = ContentScale.Crop,
            imageType = ImageType.Poster(),
            modifier = Modifier.constrainAs(posterRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        )

        Text(
            text = anime.name,
            style = MaterialTheme.typography.labelSmall,
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(posterRef.bottom, margin = 4.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = "${UserRateMapper.mapAnimeKind(anime.kind)} • ${anime.score}★",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 10.sp
            ),
            modifier = Modifier.constrainAs(infoRef) {
                top.linkTo(titleRef.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}