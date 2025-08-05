package com.example.shikiflow.utils

object FlagConverter {
    private val flags = mapOf(
        "en" to "🇬🇧",
        "uk" to "🇺🇦",
        "ru" to "🇷🇺",
        "pt-br" to "🇧🇷",
        "pt" to "🇵🇹",
        "es" to "🇪🇸",
        "fr" to "🇫🇷",
        "de" to "🇩🇪",
        "it" to "🇮🇹",
        "ja" to "🇯🇵",
        "ko" to "🇰🇷",
        "zh" to "🇨🇳",
        "zh-hk" to "🇭🇰",
        "ar" to "🇸🇦",
        "pl" to "🇵🇱",
        "nl" to "🇳🇱",
        "sv" to "🇸🇪",
        "da" to "🇩🇰",
        "no" to "🇳🇴",
        "fi" to "🇫🇮",
        "cs" to "🇨🇿",
        "es-la" to "🇲🇽",
        "hu" to "🇭🇺",
        "id" to "🇮🇩",
        "ms" to "🇲🇾",
        "mn" to "🇲🇳",
        "kk" to "🇰🇿",
        "fa" to "🇮🇷",
        "tr" to "🇹🇷",
        "bg" to "🇧🇬",
        "ro" to "🇷🇴",
        "ur" to "🇵🇰",
        "vi" to "🇻🇳",
        "th" to "🇹🇭",
        "hi" to "🇮🇳",
        "uy" to "🇺🇾",
        "he" to "🇮🇱",
        "hr" to "🇭🇷",
        "sr" to "🇷🇸",
        "el" to "🇬🇷"
    )

    fun getFlag(languageCode: String): String {
        return flags[languageCode.lowercase()] ?: languageCode
    }
}