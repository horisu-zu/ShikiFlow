package com.example.shikiflow.presentation.screen.more.history

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.shikiflow.data.user.UserHistoryResponse
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.utils.BuildConfig
import com.example.shikiflow.utils.Converter
import kotlinx.datetime.toInstant

@Composable
fun HistoryItem(
    historyItem: UserHistoryResponse?,
    modifier: Modifier = Modifier
) {
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 12.dp, end = 24.dp)
    ) {
        val (posterRef, titleRef, actionRef, dateRef) = createRefs()

        BaseImage(
            model = "${BuildConfig.BASE_URL}${historyItem?.target?.image?.original}",
            contentDescription = "Poster",
            modifier = Modifier
                .width(96.dp)
                .constrainAs(posterRef) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                }
        )

        Text(
            text = historyItem?.target?.name ?: "Huh!",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.constrainAs(titleRef) {
                top.linkTo(parent.top)
                start.linkTo(posterRef.end, margin = 16.dp)
                end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            }
        )

        Text(
            text = historyItem?.description?.replace("<b>", "")
                ?.replace("</b>", "") ?: "Huh!",
            style = MaterialTheme.typography.bodySmall,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(actionRef) {
                top.linkTo(titleRef.bottom, margin = 4.dp)
                start.linkTo(titleRef.start)
            }
        )

        Text(
            text = Converter.formatInstant(historyItem?.created_at?.toInstant()),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            modifier = Modifier.constrainAs(dateRef) {
                top.linkTo(actionRef.bottom, margin = 2.dp)
                start.linkTo(titleRef.start)
            }
        )
    }
}
