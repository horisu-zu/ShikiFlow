package com.example.shikiflow.domain.model.user

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import com.example.shikiflow.R
import com.example.shikiflow.utils.IconResource

enum class FavoriteCategory(val titleResId: Int, val iconRes: IconResource) {
    ANIME(R.string.main_track_mode_anime, IconResource.Drawable(R.drawable.ic_anime)),
    MANGA(R.string.main_track_mode_manga, IconResource.Drawable(R.drawable.ic_manga)),
    CHARACTER(R.string.details_characters, IconResource.Drawable(R.drawable.ic_character)),
    STAFF(R.string.favorite_staff, IconResource.Vector(Icons.Default.Person)),
    STUDIO(R.string.favorite_studios, IconResource.Drawable(R.drawable.ic_film_camera)),
    SEYU(R.string.shiki_person_type_seyu, IconResource.Drawable(R.drawable.ic_microphone)),
    MANGAKA(R.string.shiki_person_type_mangaka, IconResource.Drawable(R.drawable.ic_drawing)),
    PRODUCER(R.string.shiki_person_type_producer, IconResource.Drawable(R.drawable.ic_film_camera)),
    OTHER_PERSON(R.string.shiki_person_type_other, IconResource.Vector(Icons.Default.Person));
}