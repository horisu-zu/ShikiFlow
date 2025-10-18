package com.example.shikiflow.domain.model.mapper

import com.example.graphql.type.OrderEnum
import com.example.graphql.type.SeasonString
import com.example.shikiflow.domain.model.anime.MyListString

data class BrowseOptions(
    val name: String? = null,
    val studio: String? = null,
    val status: Enum<*>? = null,
    val order: OrderEnum? = OrderEnum.ranked,
    val kind: Enum<*>? = null,
    val season: SeasonString? = null,
    val genre: String? = null,
    val userListStatus: List<MyListString> = emptyList()
)