package com.example.shikiflow.presentation.common.mappers

import androidx.compose.ui.graphics.Color
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.auth.AuthType
import com.example.shikiflow.utils.IconResource

object AuthTypeMapper {
    fun AuthType.displayValue(): Int {
        return when(this) {
            AuthType.SHIKIMORI -> R.string.auth_type_shikimori
            AuthType.ANILIST -> R.string.auth_type_anilist
        }
    }

    fun AuthType.iconResource(): IconResource {
        return when(this) {
            AuthType.SHIKIMORI -> IconResource.Drawable(R.drawable.shiki_logo)
            AuthType.ANILIST -> IconResource.Drawable(R.drawable.anilist_logo)
        }
    }

    fun AuthType.colors(): Pair<Color, Color> {
        return when(this) {
            AuthType.SHIKIMORI -> Color(0xFFFFFFFF) to Color(0xFF000000)
            AuthType.ANILIST ->  Color(0xFF152232) to Color(0xFF02A9FF)
        }
    }
}