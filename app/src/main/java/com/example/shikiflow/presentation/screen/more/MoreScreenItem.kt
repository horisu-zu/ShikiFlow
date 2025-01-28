package com.example.shikiflow.presentation.screen.more

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.shikiflow.presentation.common.Image
import com.example.shikiflow.utils.IconResource

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        modifier = modifier,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun ExpandedItem(
    avatar: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        val (avatarRef, titleRef, subtitleRef) = createRefs()

        Image(
            model = avatar,
            contentDescription = "Avatar",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .constrainAs(avatarRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
        )

        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(avatarRef.end, margin = 16.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = subtitle,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            modifier = Modifier.constrainAs(subtitleRef) {
                bottom.linkTo(parent.bottom)
                start.linkTo(titleRef.start)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}

@Composable
fun GeneralItem(
    icon: IconResource,
    title: String,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        val (iconRef, titleRef) = createRefs()

        when (icon) {
            is IconResource.Drawable -> Icon(
                painter = painterResource(id = icon.resId),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .constrainAs(iconRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
                tint = MaterialTheme.colorScheme.onSurface
            )

            is IconResource.Vector -> Icon(
                imageVector = icon.imageVector,
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .constrainAs(iconRef) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    },
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(iconRef.end, margin = 16.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )
    }
}