package com.example.shikiflow.presentation.screen.more.compare

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.ComparisonType
import com.example.shikiflow.presentation.common.shimmerEffect

@Composable
fun MediaComparisonHeader(
    currentUserNickname: String,
    targetUserNickname: String,
    count: Int,
    comparisonType: ComparisonType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clickable { onClick() }
                .padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildString {
                    append(
                        when(comparisonType) {
                            ComparisonType.SHARED -> stringResource(R.string.compare_shared_rates)
                            ComparisonType.CURRENT_USER_ONLY -> stringResource(
                                R.string.compare_unique_rates,
                                currentUserNickname
                            )
                            ComparisonType.TARGET_USER_ONLY -> stringResource(
                                R.string.compare_unique_rates,
                                targetUserNickname
                            )
                        }
                    )
                    append(" — $count")
                },
                style = MaterialTheme.typography.titleSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(2f)
            )

            VerticalDivider(
                color = MaterialTheme.colorScheme.background,
                thickness = 2.dp,
                modifier = Modifier.fillMaxHeight()
            )

            if(comparisonType != ComparisonType.TARGET_USER_ONLY) {
                Text(
                    text = currentUserNickname,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            if(comparisonType == ComparisonType.SHARED) {
                VerticalDivider(
                    color = MaterialTheme.colorScheme.background,
                    thickness = 2.dp,
                    modifier = Modifier.fillMaxHeight()
                )
            }

            if(comparisonType != ComparisonType.CURRENT_USER_ONLY) {
                Text(
                    text = targetUserNickname,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun MediaComparisonHeaderPlaceholder(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(IntrinsicSize.Min)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(2f)
        ) {
            Box(
                modifier = Modifier
                    .width(128.dp)
                    .height(MaterialTheme.typography.titleSmall.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )
        }

        VerticalDivider(
            color = MaterialTheme.colorScheme.background,
            thickness = 2.dp,
            modifier = Modifier.fillMaxHeight()
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )
        }

        VerticalDivider(
            color = MaterialTheme.colorScheme.background,
            thickness = 2.dp,
            modifier = Modifier.fillMaxHeight()
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(MaterialTheme.typography.bodySmall.lineHeight.value.dp)
                    .clip(RoundedCornerShape(percent = 32))
                    .shimmerEffect()
            )
        }
    }
}