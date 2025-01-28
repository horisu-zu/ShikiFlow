package com.example.shikiflow.presentation.screen.more

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.shikiflow.data.common.SectionItem

@Composable
fun Section(
    modifier: Modifier = Modifier,
    title: String? = null,
    items: List<SectionItem>
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val (titleRef, contentRef) = createRefs()

        if (title != null) {
            SectionTitle(
                title = title,
                modifier = Modifier
                    .constrainAs(titleRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                    }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(contentRef) {
                    top.linkTo(titleRef.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        ) {
            items.forEachIndexed { _, item ->
                when (item) {
                    is SectionItem.Expanded -> ExpandedItem(
                        avatar = item.avatar,
                        title = item.title,
                        subtitle = item.subtitle,
                        onClick = item.onClick
                    )
                    is SectionItem.General -> GeneralItem(
                        icon = item.icon,
                        title = item.title,
                        onClick = item.onClick
                    )
                }
            }
        }
    }
}