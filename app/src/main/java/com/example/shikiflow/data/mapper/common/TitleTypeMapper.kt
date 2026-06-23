package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.UserTitleLanguage
import com.example.shikiflow.domain.model.media_details.PreferredTitleType

object TitleTypeMapper {
    fun UserTitleLanguage.toDomainType(): PreferredTitleType {
        return when (this) {
            UserTitleLanguage.ROMAJI, UserTitleLanguage.ROMAJI_STYLISED -> PreferredTitleType.ROMAJI
            UserTitleLanguage.ENGLISH, UserTitleLanguage.ENGLISH_STYLISED -> PreferredTitleType.ENGLISH
            UserTitleLanguage.NATIVE, UserTitleLanguage.NATIVE_STYLISED -> PreferredTitleType.NATIVE
            else -> PreferredTitleType.ROMAJI
        }
    }

    fun PreferredTitleType.toAnilistType(): UserTitleLanguage {
        return when (this) {
            PreferredTitleType.ROMAJI -> UserTitleLanguage.ROMAJI
            PreferredTitleType.ENGLISH -> UserTitleLanguage.ENGLISH
            PreferredTitleType.NATIVE -> UserTitleLanguage.NATIVE
            else -> UserTitleLanguage.UNKNOWN__
        }
    }
}