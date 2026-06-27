package com.example.shikiflow.presentation.screen.more.profile.social

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.shikiflow.domain.model.user.User
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.presentation.common.image.ImageType
import com.example.shikiflow.presentation.common.shimmerEffect

@Composable
fun UserSocialItem(
    user: User,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BaseImage(
            model = user.avatarUrl,
            imageType = ImageType.Square(
                width = 48.dp,
                shape = RoundedCornerShape(18)
            )
        )
        Text(
            text = user.nickname,
            style = MaterialTheme.typography.bodySmall.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun UserSocialItemPlaceholder(
    index: Int,
    modifier: Modifier = Modifier
) {
    val indexValue = index % 3 + 1
    val imageType = ImageType.Square(
        width = 48.dp,
        shape = RoundedCornerShape(18)
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(imageType.width)
                .clip(imageType.shape)
                .shimmerEffect()
        )

        Box(
            modifier = Modifier
                .width(36.dp + indexValue * 12.dp)
                .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                .clip(RoundedCornerShape(percent = 32))
                .shimmerEffect()
        )
    }
}