package com.example.shikiflow.domain.model.common

enum class SortDirection {
    ASCENDING,
    DESCENDING;

    companion object {
        fun SortDirection.changeDirection(): SortDirection {
            return when(this) {
                ASCENDING -> DESCENDING
                DESCENDING -> ASCENDING
            }
        }
    }
}