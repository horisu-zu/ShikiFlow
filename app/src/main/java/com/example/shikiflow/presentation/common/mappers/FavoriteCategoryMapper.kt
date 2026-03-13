package com.example.shikiflow.presentation.common.mappers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.user.FavoriteCategory
import com.example.shikiflow.presentation.common.TabRowItem
import com.example.shikiflow.utils.IconResource

object FavoriteCategoryMapper {
    fun FavoriteCategory.displayValue(): Int {
        return when(this) {
            FavoriteCategory.ANIME -> R.string.media_type_anime
            FavoriteCategory.MANGA -> R.string.media_type_manga
            FavoriteCategory.CHARACTER -> R.string.details_characters
            FavoriteCategory.STAFF -> R.string.favorite_staff
            FavoriteCategory.STUDIO -> R.string.favorite_studios
            FavoriteCategory.SEYU -> R.string.shiki_person_type_seyu
            FavoriteCategory.MANGAKA -> R.string.shiki_person_type_mangaka
            FavoriteCategory.PRODUCER -> R.string.shiki_person_type_producer
            FavoriteCategory.OTHER_PERSON -> R.string.shiki_person_type_other
        }
    }

    fun FavoriteCategory.iconResource(): IconResource {
        return when(this) {
            FavoriteCategory.ANIME -> IconResource.Drawable(R.drawable.ic_anime)
            FavoriteCategory.MANGA -> IconResource.Drawable(R.drawable.ic_manga)
            FavoriteCategory.CHARACTER -> IconResource.Drawable(R.drawable.ic_character)
            FavoriteCategory.STAFF -> IconResource.Vector(Icons.Default.Person)
            FavoriteCategory.STUDIO -> IconResource.Drawable(R.drawable.ic_film_camera)
            FavoriteCategory.SEYU -> IconResource.Drawable(R.drawable.ic_microphone)
            FavoriteCategory.MANGAKA -> IconResource.Drawable(R.drawable.ic_drawing)
            FavoriteCategory.PRODUCER -> IconResource.Drawable(R.drawable.ic_film_camera)
            FavoriteCategory.OTHER_PERSON -> IconResource.Vector(Icons.Default.Person)
        }
    }

    fun FavoriteCategory.toTabRowItem(): TabRowItem<FavoriteCategory> {
        return TabRowItem(
            value = this,
            titleRes = this.displayValue(),
            iconResource = this.iconResource()
        )
    }
}