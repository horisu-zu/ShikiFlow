package com.example.shikiflow.presentation.screen.more.settings

import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.domain.model.user.User as DomainUser
import com.example.shikiflow.utils.IconResource

sealed interface SectionItem {
    val title: String
    val displayValue: String?

    data class Default(
        override val title: String,
        override val displayValue: String,
        val onClick: () -> Unit,
        val isVisible: Boolean = true
    ): SectionItem

    data class Switch(
        override val title: String,
        override val displayValue: String,
        val onClick: () -> Unit,
        val isChecked: Boolean,
        val isVisible: Boolean = true
    ): SectionItem

    data class User(
        override val title: String,
        override val displayValue: String,
        val authType: AuthType?,
        val onClick: () -> Unit,
        val imageUrl: String
    ): SectionItem

    data class TrackerServices(
        override val title: String,
        override val displayValue: String? = null,
        val currentAuthType: AuthType?,
        val serviceUpdateState: Boolean,
        val connectedServicesMap: Map<AuthType, DomainUser>,
        val onServiceClick: (AuthType) -> Unit,
        val onServiceUpdateToggle: () -> Unit
    ): SectionItem

    /*data class Theme(
        override val title: String,
        val onClick: (ThemeMode) -> Unit,
        val themeMode: ThemeMode
    ): SectionItem {
        override val displayValue: String
            get() = themeMode.name.lowercase().replaceFirstChar { it.uppercase() }
    }*/

    data class Mode(
        override val title: String,
        val entries: List<String>,
        val iconResources: List<IconResource> = emptyList(),
        val weights: List<Float> = emptyList(),
        val onClick: (Int) -> Unit,
        val mode: String,
        val isVisible: Boolean = true
    ): SectionItem {
        override val displayValue: String
            get() = mode.lowercase().replaceFirstChar { it.uppercase() }
    }
}