package com.example.shikiflow.domain.model.favorite

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.anime.ShikiAnime
import com.example.shikiflow.domain.model.anime.ShikiManga
import com.example.shikiflow.domain.model.character.ShikiCharacter
import com.example.shikiflow.domain.model.person.ShikiPerson

sealed class ShikiFavorite {
    data class FavoriteAnime(val shikiAnime: ShikiAnime): ShikiFavorite()
    data class FavoriteManga(val shikiManga: ShikiManga): ShikiFavorite()
    data class FavoriteCharacter(val shikiCharacter: ShikiCharacter): ShikiFavorite()
    data class FavoritePerson(val personType: PersonType, val shikiPerson: ShikiPerson):
        ShikiFavorite()
}

enum class PersonType(val resId: Int) {
    SEYU(R.string.shiki_person_type_seyu),
    MANGAKA(R.string.shiki_person_type_mangaka),
    PRODUCER(R.string.shiki_person_type_producer),
    OTHER(R.string.shiki_person_type_other)
}

