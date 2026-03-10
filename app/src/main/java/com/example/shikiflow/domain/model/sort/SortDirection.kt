package com.example.shikiflow.domain.model.sort

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