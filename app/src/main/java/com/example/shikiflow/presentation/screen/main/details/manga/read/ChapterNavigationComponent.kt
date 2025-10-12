package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun ChapterNavigationComponent(
    currentPage: Int,
    pageCount: Int,
    onNavigateClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onInteractionStart: () -> Unit = {},
    onInteractionEnd: () -> Unit = {}
) {
    var pageInput by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val imeBottom = WindowInsets.ime.getBottom(LocalDensity.current)

    val displayValue = if (isEditing) pageInput else currentPage.toString()

    LaunchedEffect(imeBottom) {
        if (imeBottom == 0) {
            focusManager.clearFocus()
        }
    }

    Row(
        modifier = modifier.wrapContentWidth().clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.75f)),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        IconButton(
            onClick = { onNavigateClick(currentPage - 1) },
            enabled = currentPage > 1,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Page",
                modifier = Modifier.size(24.dp)
            )
        }
        BasicTextField(
            value = displayValue,
            onValueChange = { newValue ->
                if (!isEditing) {
                    isEditing = true
                    pageInput = currentPage.toString()
                }
                pageInput = newValue.filter { it.isDigit() }
            },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            modifier = Modifier.width(32.dp).onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    onInteractionStart()
                    if (!isEditing) {
                        isEditing = true
                        pageInput = currentPage.toString()
                    }
                } else {
                    onInteractionEnd()
                    if (isEditing) {
                        pageInput.toIntOrNull()?.let { targetPage ->
                            if (targetPage in 1..pageCount) {
                                if(targetPage != currentPage) {
                                    onNavigateClick(targetPage)
                                }
                            }
                        }
                        isEditing = false
                    }
                }
            }, decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) { innerTextField() }
            }
        )
        Text(
            text = "/ $pageCount",
            style = MaterialTheme.typography.bodyMedium
        )
        IconButton(
            onClick = { onNavigateClick(currentPage + 1) },
            enabled = currentPage < pageCount,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Page",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}