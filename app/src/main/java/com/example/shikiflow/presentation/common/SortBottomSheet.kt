package com.example.shikiflow.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shikiflow.domain.model.sort.Sort
import com.example.shikiflow.domain.model.sort.SortDirection
import com.example.shikiflow.domain.model.sort.SortType
import com.example.shikiflow.presentation.common.mappers.SortMapper.displayValue

data class SortConfig<T : SortType>(
    val options: List<T>,
    val selected: Sort<T>,
    val onSortChange: (Sort<T>) -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T : SortType> SortBottomSheet(
    config: SortConfig<T>,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        sheetState = sheetState,
        dragHandle = null,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            AnimatedVisibility(
                visible = config.selected.type.supportsDirection,
            ) {
                SortDirectionItem(
                    currentDirection = config.selected.direction,
                    onDirectionToggle = { direction ->
                        if(direction != config.selected.direction) {
                            config.onSortChange(
                                Sort(
                                    type = config.selected.type,
                                    direction = direction
                                )
                            )
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            config.options.forEach { sortType ->
                val onOptionClick = {
                    if(sortType != config.selected.type) {
                        config.onSortChange(
                            Sort(
                                type = sortType,
                                direction = config.selected.direction
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOptionClick() },
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (sortType == config.selected.type),
                        onClick = { onOptionClick() }
                    )
                    Text(
                        text = stringResource(id = sortType.displayValue()),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun SortDirectionItem(
    currentDirection: SortDirection,
    onDirectionToggle: (SortDirection) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background)
            .padding(all = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
    ) {
        SortDirection.entries.forEach { direction ->
            val isChecked = direction == currentDirection

            Text(
                text = stringResource(direction.displayValue()),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isChecked) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = if (isChecked) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.background
                    )
                    .clickable {
                        onDirectionToggle(direction)
                    }
                    .padding(horizontal = 12.dp, vertical = 16.dp)
            )
        }
    }
}