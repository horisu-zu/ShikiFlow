package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.domain.model.user.BaseUser
import com.example.shikiflow.domain.model.user.CurrentUser
import com.example.shikiflow.domain.model.user.UserShort

object UserMapper {
    fun BaseUser.toUserShort(): UserShort {
        return when (this) {
            is CurrentUser -> UserShort(
                id = id,
                nickname = nickname,
                avatarUrl = avatarUrl,
                profileBannerUrl = profileBannerUrl
            )
            is UserShort -> this
        }
    }
}