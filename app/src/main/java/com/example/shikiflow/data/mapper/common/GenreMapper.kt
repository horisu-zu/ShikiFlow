package com.example.shikiflow.data.mapper.common

import com.example.graphql.shikimori.fragment.GenreShort
import com.example.shikiflow.domain.model.media_details.Genre as DomainGenre

object GenreMapper {
    fun GenreShort.toDomain(): DomainGenre? {
        return when(name) {
            "Suspense" -> DomainGenre.THRILLER
            "Mystery" -> DomainGenre.MYSTERY
            "Adventure" -> DomainGenre.ADVENTURE
            "Drama" -> DomainGenre.DRAMA
            "Avant Garde" -> DomainGenre.AVANT_GARDE
            "Sci-Fi" -> DomainGenre.SCI_FI
            "Sports" -> DomainGenre.SPORTS
            "Supernatural" -> DomainGenre.SUPERNATURAL
            "Gourmet" -> DomainGenre.GOURMET
            "Action" -> DomainGenre.ACTION
            "Slice of Life" -> DomainGenre.SLICE_OF_LIFE
            "Comedy" -> DomainGenre.COMEDY
            "Ecchi" -> DomainGenre.ECCHI
            "Fantasy" -> DomainGenre.FANTASY
            "Horror" -> DomainGenre.HORROR
            "Romance" -> DomainGenre.ROMANCE
            "Erotica" -> DomainGenre.EROTICA
            "Hentai" -> DomainGenre.HENTAI
            else -> null
        }
    }

    fun fromString(value: String): DomainGenre? {
        return when(value) {
            "Action" -> DomainGenre.ACTION
            "Adventure" -> DomainGenre.ADVENTURE
            "Avant Garde" -> DomainGenre.AVANT_GARDE
            "Comedy" -> DomainGenre.COMEDY
            "Drama" -> DomainGenre.DRAMA
            "Ecchi" -> DomainGenre.ECCHI
            "Erotica" -> DomainGenre.EROTICA
            "Fantasy" -> DomainGenre.FANTASY
            "Gourmet" -> DomainGenre.GOURMET
            "Hentai" -> DomainGenre.HENTAI
            "Horror" -> DomainGenre.HORROR
            "Mahou Shoujo" -> DomainGenre.MAHOU_SHOUJO
            "Mecha" -> DomainGenre.MECHA
            "Music" -> DomainGenre.MUSIC
            "Mystery" -> DomainGenre.MYSTERY
            "Psychological" -> DomainGenre.PSYCHOLOGICAL
            "Romance" -> DomainGenre.ROMANCE
            "Sci-Fi" -> DomainGenre.SCI_FI
            "Slice of Life" -> DomainGenre.SLICE_OF_LIFE
            "Sports" -> DomainGenre.SPORTS
            "Supernatural" -> DomainGenre.SUPERNATURAL
            "Thriller", "Suspense" -> DomainGenre.THRILLER
            else -> null
        }
    }
}