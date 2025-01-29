package com.example.shikiflow.utils

import androidx.compose.ui.graphics.Color
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.anime.MyListString

object StatusColor {
    fun getAnimeStatusColor(status: Any): Color = when(status) {
        is UserRateStatusEnum -> getColorForUserRateStatus(status)
        is MyListString -> getColorForMyListStatus(status)
        is String -> getColorForString(status)
        else -> Color(0xFF8C8C8C)
    }

    fun getAnimeStatusBrightColor(status: String): Color = when (status.lowercase()) {
        "watching" -> Color(0xFFAE62CF)
        "reading" -> Color(0xFFAE62CF)
        "planned" -> Color(0xFFD4C862)
        "rewatching" -> Color(0xFF62CFCF)
        "rereading" -> Color(0xFF62CFCF)
        "completed" -> Color(0xFF62CF71)
        "on_hold" -> Color(0xFF628ACF)
        "dropped" -> Color(0xFFCF6562)
        else -> Color(0xFFADADAD)
    }

    private fun getColorForString(status: String): Color = when (status.lowercase()) {
        "watching" -> Color(0xFF8F4FA8)
        "planned" -> Color(0xFFA89F4F)
        "rewatching" -> Color(0xFF4FA8A8)
        "completed" -> Color(0xFF4FA85A)
        "on_hold" -> Color(0xFF4F6FA8)
        "dropped" -> Color(0xFFA8524F)
        else -> Color(0xFF8C8C8C)
    }

    private fun getColorForUserRateStatus(status: UserRateStatusEnum): Color = when(status) {
        UserRateStatusEnum.watching -> Color(0xFF8F4FA8)
        UserRateStatusEnum.planned -> Color(0xFFA89F4F)
        UserRateStatusEnum.rewatching -> Color(0xFF4FA8A8)
        UserRateStatusEnum.completed -> Color(0xFF4FA85A)
        UserRateStatusEnum.on_hold -> Color(0xFF4F6FA8)
        UserRateStatusEnum.dropped -> Color(0xFFA8524F)
        UserRateStatusEnum.UNKNOWN__ -> Color(0xFF8C8C8C)
    }

    private fun getColorForMyListStatus(status: MyListString): Color = when(status) {
        MyListString.WATCHING -> Color(0xFF8F4FA8)
        MyListString.PLANNED -> Color(0xFFA89F4F)
        MyListString.REWATCHING -> Color(0xFF4FA8A8)
        MyListString.COMPLETED -> Color(0xFF4FA85A)
        MyListString.ON_HOLD -> Color(0xFF4F6FA8)
        MyListString.DROPPED -> Color(0xFFA8524F)
    }
}