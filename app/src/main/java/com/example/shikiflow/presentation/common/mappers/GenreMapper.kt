package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.media_details.Genre

object GenreMapper {
    fun Genre.displayValue(): Int {
        return when(this) {
            Genre.ACTION -> R.string.genre_action
            Genre.ADVENTURE -> R.string.genre_adventure
            Genre.AVANT_GARDE -> R.string.genre_avant_garde
            Genre.COMEDY -> R.string.genre_comedy
            Genre.DRAMA -> R.string.genre_drama
            Genre.ECCHI -> R.string.genre_ecchi
            Genre.EROTICA -> R.string.genre_erotica
            Genre.FANTASY -> R.string.genre_fantasy
            Genre.GOURMET -> R.string.genre_gourmet
            Genre.HENTAI -> R.string.genre_hentai
            Genre.HORROR -> R.string.genre_horror
            Genre.MAHOU_SHOUJO -> R.string.genre_mahou_shoujo
            Genre.MECHA -> R.string.genre_mecha
            Genre.MUSIC -> R.string.genre_music
            Genre.MYSTERY -> R.string.genre_mystery
            Genre.PSYCHOLOGICAL -> R.string.genre_psychological
            Genre.ROMANCE -> R.string.genre_romance
            Genre.SCI_FI -> R.string.genre_sci_fi
            Genre.SLICE_OF_LIFE -> R.string.genre_slice_of_life
            Genre.SPORTS -> R.string.genre_sports
            Genre.SUPERNATURAL -> R.string.genre_supernatural
            Genre.THRILLER -> R.string.genre_thriller
            Genre.BOYS_LOVE -> R.string.media_tag_boys_love
            Genre.GIRLS_LOVE -> R.string.genre_girls_love
            Genre.YURI -> R.string.media_tag_yuri
            Genre.YAOI -> R.string.genre_yaoi
            Genre.SHOUNEN -> R.string.media_tag_shounen
            Genre.SEINEN -> R.string.media_tag_seinen
            Genre.SHOUJO -> R.string.media_tag_shoujo
            Genre.JOSEI -> R.string.media_tag_josei
            Genre.KIDS -> R.string.media_tag_kids
        }
    }
}