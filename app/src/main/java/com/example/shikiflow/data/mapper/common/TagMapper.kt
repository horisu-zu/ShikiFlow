package com.example.shikiflow.data.mapper.common

import com.example.shikiflow.domain.model.media_details.MediaTagEnum

object TagMapper {
    private val mediaTagMap: Map<String, MediaTagEnum> by lazy {
        MediaTagEnum.entries.associateBy { it.name }
    }

    fun fromString(value: String): MediaTagEnum? {
        return when(value) {
            "Idols (Male)", "Idols (Female)" -> MediaTagEnum.IDOLS
            "4-koma" -> MediaTagEnum.FOUR_KOMA
            "LGBTQ+ Themes" -> MediaTagEnum.LGBTQ_PLUS_THEMES
            "CGDCT" -> MediaTagEnum.CUTE_GIRLS_DOING_CUTE_THINGS
            else -> {
                val normalized = value
                    .replace(" ", "_")
                    .replace("-", "_")
                    .replace("'", "")
                    .uppercase()

                mediaTagMap[normalized]
            }
        }
    }
}