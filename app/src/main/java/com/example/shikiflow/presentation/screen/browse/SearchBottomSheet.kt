package com.example.shikiflow.presentation.screen.browse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.graphql.type.OrderEnum
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.anime.BrowseType
import com.example.shikiflow.data.anime.MyListString
import com.example.shikiflow.data.mapper.BrowseOptions
import com.example.shikiflow.data.mapper.EnumUtils
import com.example.shikiflow.data.search.ContentType
import com.example.shikiflow.presentation.common.ChipSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    currentType: BrowseType,
    searchOptions: BrowseOptions,
    onTypeChanged: (BrowseType) -> Unit,
    onOptionsChanged: (BrowseOptions) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isAnime = currentType is BrowseType.AnimeBrowseType
    val contentType = remember(currentType) {
        ContentType.fromBrowseType(currentType)
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)
        ) {
            ChipSection(
                label = "Media",
                items = listOf("Anime", "Manga"),
                selectedItems = listOf(if (isAnime) "Anime" else "Manga"),
                onItemSelected = { formattedName ->
                    val newType = if (formattedName == "Anime") {
                        BrowseType.AnimeBrowseType.SEARCH
                    } else {
                        BrowseType.MangaBrowseType.SEARCH
                    }
                    onTypeChanged(newType)
                },
                required = true
            )
            contentType.let { type ->
                ChipSection(
                    label = "Kind",
                    items = EnumUtils.getFormattedEnumList(type.kindEnum),
                    selectedItems = searchOptions.kind?.let {
                        listOf(EnumUtils.formatEnumName(it))
                    } ?: emptyList(),
                    onItemSelected = { formattedName ->
                        EnumUtils.findEnumByFormattedName(type.kindEnum, formattedName)?.let { kind ->
                            onOptionsChanged(searchOptions.copy(
                                kind = if (searchOptions.kind == kind) null else kind
                            ))
                        }
                    }
                )
            }
            contentType.let { type ->
                ChipSection(
                    label = "Status",
                    items = EnumUtils.getFormattedEnumList(type.statusEnum),
                    selectedItems = searchOptions.status?.let {
                        listOf(EnumUtils.formatEnumName(it))
                    } ?: emptyList(),
                    onItemSelected = { formattedName ->
                        EnumUtils.findEnumByFormattedName(type.statusEnum, formattedName)?.let { status ->
                            onOptionsChanged(searchOptions.copy(
                                status = if (searchOptions.status == status) null else status
                            ))
                        }
                    }
                )
            }
            ChipSection(
                label = "In my list",
                items = EnumUtils.getFormattedEnumList(UserRateStatusEnum::class),
                selectedItems = searchOptions.userListStatus.map {
                    EnumUtils.formatEnumName(it)
                },
                onItemSelected = { formattedName ->
                    EnumUtils.findEnumByFormattedName(MyListString::class, formattedName)?.let { status ->
                        val newList = if (status in searchOptions.userListStatus) {
                            searchOptions.userListStatus - status
                        } else {
                            searchOptions.userListStatus + status
                        }
                        onOptionsChanged(searchOptions.copy(userListStatus = newList))
                    }
                }
            )
            ChipSection(
                label = "Sort by",
                items = EnumUtils.getFormattedEnumList(OrderEnum::class),
                selectedItems = searchOptions.order?.let {
                    listOf(EnumUtils.formatEnumName(it))
                } ?: emptyList(),
                onItemSelected = { formattedName ->
                    EnumUtils.findEnumByFormattedName(OrderEnum::class, formattedName)?.let { order ->
                        onOptionsChanged(searchOptions.copy(
                            order = if (searchOptions.order == order) null else order
                        ))
                    }
                }
            )
        }
    }
}

/*
private fun <T> toggleItem(items: List<T>, item: T): List<T> {
    return if (items.contains(item)) {
        items.minus(item)
    } else {
        items.plus(item)
    }
}

private fun <T> toggleSingleItem(currentSelection: T?, newItem: T): T? {
    return if (currentSelection == newItem) null else newItem
}*/
