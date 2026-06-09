package com.example.shikiflow.data.mapper.common

import com.example.shikiflow.domain.model.media_details.MediaTagEnum

object TagMapper {
    private val mediaTagMap: Map<String, MediaTagEnum> by lazy {
        MediaTagEnum.entries.associateBy { it.name }
    }

    fun fromString(value: String): MediaTagEnum? {
        return when(value) {
            "Idol", "Idols (Male)", "Idols (Female)" -> MediaTagEnum.IDOLS
            "Anthropomorphism" -> MediaTagEnum.ANTHROPOMORPHIC
            "Video Games" -> MediaTagEnum.VIDEO_GAME
            "Magical Sex Shift" -> MediaTagEnum.GENDER_BENDING
            "Criminal Organization" -> MediaTagEnum.ORGANIZED_CRIME
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

    fun MediaTagEnum.toAnilistTag(): String? {
        return when(this) {
            MediaTagEnum.ANTHROPOMORPHIC -> "Anthropomorphism"
            MediaTagEnum.ANTI_HERO -> "Anti-Hero"
            MediaTagEnum.ECO_HORROR -> "Eco-Horror"
            MediaTagEnum.E_SPORTS -> "E-Sports"
            MediaTagEnum.FOUR_KOMA -> "4-koma"
            MediaTagEnum.HIP_HOP_MUSIC -> "Hip-hop Music"
            MediaTagEnum.IDOLS -> "Idol"
            MediaTagEnum.LGBTQ_PLUS_THEMES -> "LGBTQ+ Themes"
            MediaTagEnum.NON_FICTION -> "Non-fiction"
            MediaTagEnum.OJOU_SAMA -> "Ojou-sama"
            MediaTagEnum.ORGANIZED_CRIME -> "Criminal Organization"
            MediaTagEnum.POST_APOCALYPTIC -> "Post-Apocalyptic"
            MediaTagEnum.SHORT_FORM_CHAPTER -> "Short-Form Chapter"
            MediaTagEnum.VIDEO_GAME -> "Video Games"
            MediaTagEnum.BOYS_LOVE -> "Boys' Love"
            MediaTagEnum.TEENS_LOVE -> "Teens' Love"
            else -> this.name
                .replace("_", " ")
                .lowercase()
                .split(" ")
                .joinToString(" ") { it.replaceFirstChar { c -> c.uppercase() } }
        }
    }

    fun MediaTagEnum.toShikimoriTag(): String? {
        return when(this) {
            MediaTagEnum.SHOUNEN -> "27"
            MediaTagEnum.SHOUJO -> "25"
            MediaTagEnum.SEINEN -> "42"
            MediaTagEnum.JOSEI -> "43"
            MediaTagEnum.KIDS -> "15"
            MediaTagEnum.URBAN_FANTASY -> "197"
            MediaTagEnum.VILLAINESS -> "198"
            MediaTagEnum.SCHOOL -> "23"
            MediaTagEnum.ADULT_CAST -> "104"
            MediaTagEnum.DETECTIVE -> "39"
            MediaTagEnum.STRATEGY_GAME -> "11"
            MediaTagEnum.VISUAL_ARTS -> "108"
            MediaTagEnum.IDOLS -> "145"
            MediaTagEnum.ORGANIZED_CRIME -> "138"
            MediaTagEnum.DELINQUENTS -> "131"
            MediaTagEnum.REVERSE_HAREM -> "125"
            MediaTagEnum.ISEKAI -> "130"
            MediaTagEnum.EDUCATIONAL -> "149"
            MediaTagEnum.CHILDCARE -> "134"
            MediaTagEnum.PARODY -> "20"
            MediaTagEnum.PERFORMING_ARTS -> "142"
            MediaTagEnum.VIDEO_GAME -> "103"
            MediaTagEnum.PETS -> "148"
            MediaTagEnum.MAHOU_SHOUJO -> "124"
            MediaTagEnum.GENDER_BENDING -> "135"
            MediaTagEnum.SPACE -> "29"
            MediaTagEnum.ANTHROPOMORPHIC -> "143"
            MediaTagEnum.TEAM_SPORTS -> "102"
            MediaTagEnum.LOVE_POLYGON -> "107"
            MediaTagEnum.SUPER_POWER -> "31"
            MediaTagEnum.MILITARY -> "38"
            MediaTagEnum.VAMPIRE -> "32"
            MediaTagEnum.PSYCHOLOGICAL -> "40"
            MediaTagEnum.SURVIVAL -> "141"
            MediaTagEnum.REINCARNATION -> "106"
            MediaTagEnum.CROSSDRESSING -> "144"
            MediaTagEnum.CUTE_GIRLS_DOING_CUTE_THINGS -> "119"
            MediaTagEnum.MEDICAL -> "147"
            MediaTagEnum.HAREM -> "35"
            MediaTagEnum.SAMURAI -> "21"
            MediaTagEnum.HISTORICAL -> "13"
            MediaTagEnum.OTAKU_CULTURE -> "137"
            MediaTagEnum.TIME_TRAVEL -> "111"
            MediaTagEnum.LOVE_STATUS_QUO -> "151"
            MediaTagEnum.RACING -> "3"
            MediaTagEnum.SHOWBIZ -> "136"
            MediaTagEnum.AWARD_WINNING -> "114"
            MediaTagEnum.GORE -> "105"
            MediaTagEnum.WORKPLACE -> "139"
            MediaTagEnum.IYASHIKEI -> "140"
            MediaTagEnum.MUSIC -> "19"
            MediaTagEnum.GAG_HUMOR -> "112"
            MediaTagEnum.MARTIAL_ARTS -> "17"
            MediaTagEnum.HIGH_STAKES_GAME -> "146"
            MediaTagEnum.MECHA -> "18"
            MediaTagEnum.MYTHOLOGY -> "6"
            MediaTagEnum.COMBAT_SPORTS -> "118"
            else -> null
        }
    }
}