package com.example.shikiflow.domain.model.user

import com.example.shikiflow.domain.model.anime.ShikiAnime
import com.example.shikiflow.domain.model.anime.ShikiManga
import com.example.shikiflow.domain.model.character.ShikiCharacter
import com.example.shikiflow.domain.model.favorite.PersonType
import com.example.shikiflow.domain.model.favorite.ShikiFavorite
import com.example.shikiflow.domain.model.person.ShikiPerson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserFavoritesResponse(
    @SerialName("animes") val animeList: List<ShikiAnime>?,
    @SerialName("mangas") val mangaList: List<ShikiManga>?,
    @SerialName("ranobe") val ranobeList: List<ShikiManga>?,
    val characters: List<ShikiCharacter>?,
    val mangakas: List<ShikiPerson>?,
    @SerialName("seyu") val seyuList: List<ShikiPerson>?,
    val producers: List<ShikiPerson>?,
    val people: List<ShikiPerson>?
) {
    companion object {
        fun UserFavoritesResponse.toDomain(): List<ShikiFavorite> {
            return buildList {
                animeList?.forEach { add(ShikiFavorite.FavoriteAnime(it)) }
                mangaList?.forEach { add(ShikiFavorite.FavoriteManga(it)) }
                ranobeList?.forEach { add(ShikiFavorite.FavoriteManga(it)) }
                characters?.forEach { add(ShikiFavorite.FavoriteCharacter(it)) }
                mangakas?.forEach { add(ShikiFavorite.FavoritePerson(PersonType.MANGAKA, it)) }
                seyuList?.forEach { add(ShikiFavorite.FavoritePerson(PersonType.SEYU, it)) }
                producers?.forEach { add(ShikiFavorite.FavoritePerson(PersonType.PRODUCER, it)) }
                people?.forEach { add(ShikiFavorite.FavoritePerson(PersonType.OTHER, it)) }
            }
        }
    }
}

