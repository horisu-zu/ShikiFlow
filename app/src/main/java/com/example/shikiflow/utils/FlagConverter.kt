package com.example.shikiflow.utils

import java.util.Locale

object FlagConverter {
    private val flags = mapOf(
        "en" to "🇬🇧",
        "uk" to "🇺🇦",
        "ru" to "🇷🇺",
        "af" to "🇿🇦",
        "sq" to "🇦🇱",
        "ar" to "🇸🇦",
        "az" to "🇦🇿",
        "eu" to "🇪🇸",
        "be" to "🇧🇾",
        "bn" to "🇧🇩",
        "bg" to "🇧🇬",
        "my" to "🇲🇲",
        "ca" to "🇦🇩",
        "zh" to "🇨🇳",
        "zh-tw" to "🇹🇼",
        "cv" to "🌐",
        "hr" to "🇭🇷",
        "cs" to "🇨🇿",
        "da" to "🇩🇰",
        "nl" to "🇳🇱",
        "eo" to "🌐",
        "et" to "🇪🇪",
        "fil" to "🇵🇭",
        "fi" to "🇫🇮",
        "fr" to "🇫🇷",
        "ka" to "🇬🇪",
        "de" to "🇩🇪",
        "el" to "🇬🇷",
        "he" to "🇮🇱",
        "hi" to "🇮🇳",
        "hu" to "🇭🇺",
        "id" to "🇮🇩",
        "ga" to "🇮🇪",
        "it" to "🇮🇹",
        "ja" to "🇯🇵",
        "jv" to "🇮🇩",
        "kk" to "🇰🇿",
        "ko" to "🇰🇷",
        "la" to "🌐",
        "lt" to "🇱🇹",
        "ms" to "🇲🇾",
        "mn" to "🇲🇳",
        "ne" to "🇳🇵",
        "no" to "🇳🇴",
        "fa" to "🇮🇷",
        "pl" to "🇵🇱",
        "pt" to "🇵🇹",
        "pt-br" to "🇧🇷",
        "ro" to "🇷🇴",
        "sr" to "🇷🇸",
        "sk" to "🇸🇰",
        "sl" to "🇸🇮",
        "es" to "🇪🇸",
        "es-la" to "🇲🇽",
        "sv" to "🇸🇪",
        "ta" to "🇮🇳",
        "te" to "🇮🇳",
        "th" to "🇹🇭",
        "tr" to "🇹🇷",
        "ur" to "🇵🇰",
        "uz" to "🇺🇿",
        "vi" to "🇻🇳"
    )

    fun getFlag(languageCode: String): String {
        return flags[languageCode.lowercase()] ?: languageCode
    }

    val locales: Map<String, String> by lazy {
        flags.keys.associateWith { key ->
            val displayTag = if (key == "es-la") "es-MX" else key
            Locale.forLanguageTag(displayTag).displayName
        }
    }
}