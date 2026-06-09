package com.example.shikiflow.data.mapper.common

import com.example.shikiflow.domain.model.media_details.Genre as DomainGenre

object GenreMapper {
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
            "Shounen" -> DomainGenre.SHOUNEN
            "Seinen" -> DomainGenre.SEINEN
            "Shoujo" -> DomainGenre.SHOUJO
            "Josei" -> DomainGenre.JOSEI
            "Kids" -> DomainGenre.KIDS
            else -> null
        }
    }

    fun DomainGenre.toAnilistGenre(): String? {
        return when(this) {
            DomainGenre.ACTION -> "Action"
            DomainGenre.ADVENTURE -> "Adventure"
            DomainGenre.COMEDY -> "Comedy"
            DomainGenre.DRAMA -> "Drama"
            DomainGenre.ECCHI -> "Ecchi"
            DomainGenre.FANTASY -> "Fantasy"
            DomainGenre.HENTAI -> "Hentai"
            DomainGenre.HORROR -> "Horror"
            DomainGenre.MAHOU_SHOUJO -> "Mahou Shoujo"
            DomainGenre.MECHA -> "Mecha"
            DomainGenre.MUSIC -> "Music"
            DomainGenre.MYSTERY -> "Mystery"
            DomainGenre.PSYCHOLOGICAL -> "Psychological"
            DomainGenre.ROMANCE -> "Romance"
            DomainGenre.SCI_FI -> "Sci-Fi"
            DomainGenre.SLICE_OF_LIFE -> "Slice of Life"
            DomainGenre.SPORTS -> "Sports"
            DomainGenre.SUPERNATURAL -> "Supernatural"
            DomainGenre.THRILLER -> "Thriller"
            else -> null
        }
    }

    //Schema says it allows search by genre ids only
    fun DomainGenre.toShikimoriGenre(): String? {
        return when(this) {
            DomainGenre.ACTION -> "1"
            DomainGenre.ADVENTURE -> "2"
            DomainGenre.COMEDY -> "4"
            DomainGenre.AVANT_GARDE -> "5"
            DomainGenre.MYSTERY -> "7"
            DomainGenre.DRAMA -> "8"
            DomainGenre.ECCHI -> "9"
            DomainGenre.FANTASY -> "10"
            DomainGenre.HENTAI -> "12"
            DomainGenre.HORROR -> "14"
            DomainGenre.ROMANCE -> "22"
            DomainGenre.SCI_FI -> "24"
            DomainGenre.SPORTS -> "30"
            DomainGenre.SLICE_OF_LIFE -> "36"
            DomainGenre.SUPERNATURAL -> "37"
            DomainGenre.THRILLER -> "117"
            DomainGenre.EROTICA -> "539"
            DomainGenre.GOURMET -> "543"
            DomainGenre.SHOUNEN -> "27"
            DomainGenre.SEINEN -> "25"
            DomainGenre.SHOUJO -> "42"
            DomainGenre.JOSEI -> "43"
            DomainGenre.KIDS -> "15"
            else -> null
        }
    }
}