package com.example.shikiflow.utils

import androidx.compose.ui.graphics.Color
import com.example.graphql.type.UserRateStatusEnum
import com.example.shikiflow.data.anime.MyListString

object StatusColor {
    fun getStatusColor(status: Any): Color = when(status) {
        is UserRateStatusEnum -> getColorForUserRateStatus(status)
        is MyListString -> getColorForMyListStatus(status)
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