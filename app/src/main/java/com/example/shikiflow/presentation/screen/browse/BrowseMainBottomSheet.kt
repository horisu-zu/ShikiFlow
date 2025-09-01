package com.example.shikiflow.presentation.screen.browse

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
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.shikiflow.R
import com.example.shikiflow.utils.BrowseOngoingOrder
import com.example.shikiflow.utils.BrowseUiMode
import com.example.shikiflow.utils.toIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseMainBottomSheet(
    currentBrowseMode: BrowseUiMode,
    currentOngoingMode: BrowseOngoingOrder,
    onDismiss: () -> Unit,
    onModeSelect: (BrowseUiMode) -> Unit,
    onOrderSelect: (BrowseOngoingOrder) -> Unit
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.browse_mode_select),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                BrowseUiMode.entries.forEachIndexed { index, browseEntry ->
                    SegmentedButton(
                        selected = browseEntry == currentBrowseMode,
                        onClick = { if(browseEntry != currentBrowseMode) onModeSelect(browseEntry) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = BrowseUiMode.entries.size
                        ),
                        label = {
                            Text(
                                text = stringResource(browseEntry.displayValue),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        icon = {
                            SegmentedButtonDefaults.Icon(
                                active = browseEntry == currentBrowseMode
                            ) {
                                browseEntry.icon.toIcon(
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    )
                }
            }
            Column(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                BrowseOngoingOrder.entries.forEach { option ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if(option != currentOngoingMode) onOrderSelect(option)
                            },
                        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (option == currentOngoingMode),
                            onClick = { /**/ }
                        )
                        Text(
                            text = stringResource(id = option.displayValue),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}