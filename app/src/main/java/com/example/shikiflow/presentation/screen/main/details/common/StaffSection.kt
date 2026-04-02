package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.staff.StaffShort
import com.example.shikiflow.presentation.common.SnapFlingLazyRow
import com.example.shikiflow.presentation.common.image.BaseImage
import com.example.shikiflow.utils.ignoreHorizontalParentPadding

@Composable
fun StaffSection(
    staffShortList: List<StaffShort>,
    onMediaStaffClick: () -> Unit,
    onStaffClick: (Int) -> Unit,
    horizontalPadding: Dp,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.staff_title),
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(
                onClick = { onMediaStaffClick() },
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null
                )
            }
        }
        SnapFlingLazyRow(
            modifier = Modifier
                .ignoreHorizontalParentPadding(horizontalPadding)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start)
        ) {
            items(
                items = staffShortList,
                key = { item -> item.id }
            ) { staffShort ->
                StaffItem(
                    staffShort = staffShort,
                    onStaffClick = onStaffClick
                )
            }
        }
    }
}

@Composable
fun StaffItem(
    staffShort: StaffShort,
    onStaffClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onStaffClick(staffShort.id) },
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
    ) {
        BaseImage(
            model = staffShort.imageUrl,
            modifier = Modifier.width(96.dp)
        )
        Column(
            modifier = Modifier
                .width(144.dp)
                .padding(vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
        ) {
            Text(
                text = staffShort.fullName,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start),
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.Top)
            ) {
                staffShort.roles.forEach { staffRole ->
                    Text(
                        text = staffRole,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}