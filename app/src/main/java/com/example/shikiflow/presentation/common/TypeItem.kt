package com.example.shikiflow.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.shikiflow.utils.IconResource

@Composable
fun TypeItem(
    icon: IconResource,
    type: String,
    modifier: Modifier = Modifier,
    count: String? = null,
) {
    ConstraintLayout(
        modifier = modifier
    ) {
        val (iconRef, typeRef, countRef) = createRefs()

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.onSecondary)
                .constrainAs(iconRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                },
            contentAlignment = Alignment.Center
        ) {
            when (icon) {
                is IconResource.Drawable -> Icon(
                    painter = painterResource(id = icon.resId),
                    contentDescription = "Type Icon",
                    modifier = Modifier
                        .padding(6.dp)
                        .size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                is IconResource.Vector -> Icon(
                    imageVector = icon.imageVector,
                    contentDescription = "Type Icon",
                    modifier = Modifier
                        .padding(6.dp)
                        .size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Text(
            text = type,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.constrainAs(typeRef) {
                top.linkTo(parent.top)
                bottom.linkTo(countRef.top)
                start.linkTo(iconRef.end, margin = 16.dp)
            }
        )

        count?.let {
            Text(
                text = count,
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                modifier = Modifier.constrainAs(countRef) {
                    top.linkTo(typeRef.bottom)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(typeRef.start)
                }
            )
        }
    }
}