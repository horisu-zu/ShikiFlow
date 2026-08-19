package com.example.shikiflow.presentation.screen.more.settings

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindowProvider
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.tracks.MediaType
import com.example.shikiflow.presentation.common.ConnectedButtonGroup
import com.example.shikiflow.presentation.common.CustomTextField
import com.example.shikiflow.presentation.common.TextWithIcon
import com.example.shikiflow.presentation.common.mappers.MediaTypeMapper.toTabRowItem
import com.example.shikiflow.utils.IconResource
import java.util.UUID

data class CustomList(
    val id: UUID = UUID.randomUUID(),
    val listName: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomListsBottomSheet(
    customMediaLists: Map<MediaType, List<CustomList>>,
    onSave: (Map<MediaType, List<String>>) -> Unit,
    onDismiss: () -> Unit
) {
    var currentMediaType by remember { mutableStateOf(MediaType.ANIME) }
    var lists by remember { mutableStateOf(customMediaLists) }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        (LocalView.current.parent as? DialogWindowProvider)?.window?.let { window ->
            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .animateContentSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            ConnectedButtonGroup(
                items = MediaType.entries.map { type ->
                    type.toTabRowItem()
                },
                selectedIndex = currentMediaType.ordinal,
                onItemSelection = { index ->
                    currentMediaType = MediaType.entries[index]
                },
                showText = true,
                textStyle = MaterialTheme.typography.bodyLarge
            )

            AnimatedContent(
                targetState = currentMediaType
            ) { mediaType ->
                MediaCustomListsComponent(
                    items = lists[mediaType] ?: emptyList(),
                    onNameChange = { id, newName ->
                        lists = lists.toMutableMap().apply {
                            this[mediaType] = this[mediaType].orEmpty().map { item ->
                                if (item.id == id) item.copy(listName = newName) else item
                            }
                        }
                    },
                    onDelete = { id ->
                        lists = lists.toMutableMap().apply {
                            this[mediaType] = this[mediaType].orEmpty().filterNot { it.id == id }
                        }
                    },
                    onAdd = {
                        lists = lists.toMutableMap().apply {
                            this[mediaType] = this[mediaType].orEmpty() + CustomList(listName = "")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .imePadding()
                )
            }

            AnimatedVisibility(
                visible = lists.mapValues { (_, value) ->
                    value.filter { it.listName.isNotBlank() }
                } != customMediaLists
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        shape = RoundedCornerShape(percent = 24),
                        onClick = { lists = customMediaLists },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Discard Changes"
                        )
                    }

                    IconButton(
                        shape = RoundedCornerShape(percent = 24),
                        onClick = {
                            onSave(
                                lists.mapValues { (_, value) ->
                                    value.map { list -> list.listName }
                                        .filter { it.isNotBlank() }
                                }
                            )
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_save),
                            contentDescription = "Save Changes"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MediaCustomListsComponent(
    items: List<CustomList>,
    onNameChange: (UUID, String) -> Unit,
    onDelete: (UUID) -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(
            items = items,
            key = { item -> item.id }
        ) { item ->
            CustomListItem(
                listName = item.listName,
                onChange = { newName ->
                    onNameChange(item.id, newName)
                },
                onDelete = { onDelete(item.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .animateItem()
            )
        }

        item {
            TextWithIcon(
                text = stringResource(R.string.common_add),
                iconResources = listOf(IconResource.Vector(imageVector = Icons.Default.Add)),
                style = MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(percent = 24))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .clickable { onAdd() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun CustomListItem(
    listName: String,
    onChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val textFieldState = rememberTextFieldState(
        initialText = listName
    )

    LaunchedEffect(textFieldState.text) {
        onChange(textFieldState.text.toString())
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.Start),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomTextField(
            textFieldState = textFieldState,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            placeholder = {
                Text(
                    text = "Custom List Name",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            modifier = Modifier
                .weight(1f)
                .background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(percent = 24)
                )
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(percent = 24))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Custom List",
                modifier = Modifier.size(20.dp)
            )
        }
    }
}