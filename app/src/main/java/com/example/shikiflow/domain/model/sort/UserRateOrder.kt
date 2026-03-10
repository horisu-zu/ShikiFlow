package com.example.shikiflow.domain.model.sort

data class UserRateOrder(
    val type: UserRateOrderType,
    val sort: SortDirection
)

enum class UserRateOrderType {
    ID,
    ADDED_AT,
    UPDATED_AT,
    SCORE,
    PROGRESS
}
