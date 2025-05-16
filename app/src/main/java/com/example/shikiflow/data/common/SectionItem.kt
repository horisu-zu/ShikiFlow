package com.example.shikiflow.data.common

import com.example.shikiflow.utils.IconResource

sealed class SectionItem {
    data class Expanded(
        val avatar: String,
        val title: String,
        val subtitle: String,
        val onClick: () -> Unit
    ): SectionItem()

    data class General(
        val icon: IconResource,
        val title: String,
        val subtitle: String? = null,
        val onClick: () -> Unit
    ): SectionItem()
}