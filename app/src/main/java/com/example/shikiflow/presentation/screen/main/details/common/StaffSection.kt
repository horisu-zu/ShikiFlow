package com.example.shikiflow.presentation.screen.main.details.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.MediaPersonShort
import com.example.shikiflow.presentation.common.image.BaseImage

@Composable
private fun StaffSection(
    staffRoles: List<MediaPersonShort>,
    onPersonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
    ) {
        Text(
            text = stringResource(R.string.staff_label),
            style = MaterialTheme.typography.titleMedium
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            maxItemsInEachRow = 2
        ) {
            staffRoles.forEachIndexed { index, staffRole ->
                StaffItem(
                    staffRole = staffRole,
                    onPersonClick = onPersonClick,
                    modifier = if(index % 2 == 0 && index == staffRoles.size - 1) {
                        Modifier.fillMaxWidth(0.5f)
                    } else Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StaffItem(
    staffRole: MediaPersonShort,
    onPersonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onPersonClick(staffRole.id) },
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.Start)
    ) {
        BaseImage(
            model = staffRole.imageUrl,
            modifier = Modifier.width(96.dp)
        )
        Column {
            Text(
                text = staffRole.fullName,
                style = MaterialTheme.typography.bodyMedium
            )
            /*Text(
                text = staffRole.role,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            )*/
        }
    }
}