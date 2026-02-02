package com.example.shikiflow.data.datasource.dto

import com.example.shikiflow.data.datasource.dto.person.ShikiPerson
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiUserFavoritesResponse(
    @SerialName("animes") val animeList: List<ShikiAnime>?,
    @SerialName("mangas") val mangaList: List<ShikiManga>?,
    @SerialName("ranobe") val ranobeList: List<ShikiManga>?,
    val characters: List<ShikiCharacter>?,
    val mangakas: List<ShikiPerson>?,
    @SerialName("seyu") val seyuList: List<ShikiPerson>?,
    val producers: List<ShikiPerson>?,
    val people: List<ShikiPerson>?
)