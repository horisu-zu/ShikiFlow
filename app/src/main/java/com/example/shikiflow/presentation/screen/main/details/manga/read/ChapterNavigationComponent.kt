package com.example.shikiflow.presentation.screen.main.details.manga.read

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalLayoutApi::class)
@Composable
fun ChapterNavigationComponent(
    currentPage: Int,
    pageCount: Int,
    onNavigateClick: (Int) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var pageInput by remember { mutableStateOf(currentPage.toString()) }
    var isEditing by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val isKeyboardVisible = WindowInsets.isImeVisible

    LaunchedEffect(isKeyboardVisible) {
        if(!isKeyboardVisible) {
            focusManager.clearFocus()
        }
    }

    LaunchedEffect(currentPage) {
        if(!isEditing) {
            pageInput = currentPage.toString()
        }
    }

    HorizontalFloatingToolbar(
        expanded = true,
        shape = RoundedCornerShape(size = 24.dp),
        modifier = modifier.heightIn(max = 56.dp)
    ) {
        IconButton(
            onClick = { onNavigateClick(currentPage - 1) },
            enabled = currentPage > 1
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous Page"
            )
        }
        BasicTextField(
            value = pageInput,
            onValueChange = { pageInput = it },
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier.width(40.dp)
                .onFocusChanged { focusState ->
                    if(focusState.isFocused) {
                        isEditing = true
                        onFocusChange(true)
                    } else if(isEditing) {
                        val targetPage = pageInput.toIntOrNull()

                        if (targetPage != null) {
                            val coercedValue = targetPage.coerceIn(1, pageCount)

                            if (coercedValue != currentPage) {
                                onNavigateClick(coercedValue)
                            }
                            pageInput = coercedValue.toString()
                        } else {
                            pageInput = currentPage.toString()
                        }

                        isEditing = false
                        onFocusChange(false)
                    }
                },
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier.fillMaxSize()
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) { innerTextField() }
            }
        )
        Text(
            text = "/ $pageCount",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        IconButton(
            onClick = { onNavigateClick(currentPage + 1) },
            enabled = currentPage < pageCount
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next Page"
            )
        }
    }
}