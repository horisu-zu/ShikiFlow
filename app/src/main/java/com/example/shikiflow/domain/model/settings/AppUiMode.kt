package com.example.shikiflow.domain.model.settings

enum class AppUiMode {
    LIST, GRID;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: LIST
    }
}

enum class BrowseUiMode {
    AUTO, LIST, GRID;

    companion object {
        fun fromString(value: String?) = entries.find { it.name == value } ?: AUTO
    }
}
