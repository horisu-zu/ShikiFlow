package com.example.shikiflow.domain.model.favorite

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.ShikiAnime
import com.example.shikiflow.domain.model.anime.ShikiManga
import com.example.shikiflow.domain.model.character.ShikiCharacter
import com.example.shikiflow.domain.model.person.ShikiPerson
import com.example.shikiflow.utils.IconResource

sealed class ShikiFavorite {
    abstract val category: FavoriteCategory

    data class FavoriteAnime(val shikiAnime: ShikiAnime): ShikiFavorite() {
        override val category = FavoriteCategory.ANIME
    }
    data class FavoriteManga(val shikiManga: ShikiManga): ShikiFavorite() {
        override val category = FavoriteCategory.MANGA
    }
    data class FavoriteCharacter(val shikiCharacter: ShikiCharacter): ShikiFavorite() {
        override val category = FavoriteCategory.CHARACTER
    }
    data class FavoritePerson(val personType: PersonType, val shikiPerson: ShikiPerson): ShikiFavorite() {
        override val category = FavoriteCategory.fromPersonType(personType)
    }
}

enum class FavoriteCategory(val titleResId: Int, val iconRes: IconResource) {
    ANIME(R.string.main_track_mode_anime, IconResource.Drawable(R.drawable.ic_anime)),
    MANGA(R.string.main_track_mode_manga, IconResource.Drawable(R.drawable.ic_manga)),
    CHARACTER(R.string.details_characters, IconResource.Drawable(R.drawable.ic_character)),
    SEYU(R.string.shiki_person_type_seyu, IconResource.Drawable(R.drawable.ic_microphone)),
    MANGAKA(R.string.shiki_person_type_mangaka, IconResource.Drawable(R.drawable.ic_drawing)),
    PRODUCER(R.string.shiki_person_type_producer, IconResource.Drawable(R.drawable.ic_film_camera)),
    OTHER_PERSON(R.string.shiki_person_type_other, IconResource.Vector(Icons.Default.Person));

    companion object {
        fun fromPersonType(type: PersonType) = when(type) {
            PersonType.SEYU -> SEYU
            PersonType.MANGAKA -> MANGAKA
            PersonType.PRODUCER -> PRODUCER
            PersonType.OTHER -> OTHER_PERSON
        }
    }
}

enum class PersonType(val resId: Int) {
    SEYU(R.string.shiki_person_type_seyu),
    MANGAKA(R.string.shiki_person_type_mangaka),
    PRODUCER(R.string.shiki_person_type_producer),
    OTHER(R.string.shiki_person_type_other)
}

