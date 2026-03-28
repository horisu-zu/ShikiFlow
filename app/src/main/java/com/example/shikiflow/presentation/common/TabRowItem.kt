package com.example.shikiflow.presentation.common

import androidx.annotation.StringRes
import com.example.shikiflow.utils.IconResource

data class TabRowItem<T>(
    val value: T,
    val iconResource: IconResource? = null,
    @param:StringRes val titleRes: Int? = null
)