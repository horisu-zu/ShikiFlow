package com.example.shikiflow.presentation.screen.main.details.anime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.presentation.screen.more.GeneralItem
import com.example.shikiflow.utils.IconResource

@Composable
fun MediaDetailsNavComponent(
    authType: AuthType,
    onThreadsClick: () -> Unit,
    onSimilarClick: () -> Unit,
    onExternalLinksClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start
    ) {
        if(authType == AuthType.ANILIST) {
            GeneralItem(
                icon = IconResource.Drawable(resId = R.drawable.ic_thread),
                title = stringResource(R.string.details_info_threads),
                onClick = { onThreadsClick() }
            )
        }
        GeneralItem(
            icon = IconResource.Drawable(resId = R.drawable.ic_intersection),
            title = stringResource(R.string.details_info_recommendations),
            onClick = { onSimilarClick() }
        )
        GeneralItem(
            icon = IconResource.Drawable(resId = R.drawable.ic_link),
            title = stringResource(R.string.details_info_links),
            onClick = { onExternalLinksClick() }
        )
    }
}