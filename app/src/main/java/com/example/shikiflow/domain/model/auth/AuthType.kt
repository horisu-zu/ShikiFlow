package com.example.shikiflow.domain.model.auth

import com.example.shikiflow.BuildConfig

enum class AuthType {
    SHIKIMORI,
    ANILIST;

    companion object {
        fun getAuthType(path: String): AuthType? {
            return when {
                path.contains(BuildConfig.ANILIST_REDIRECT_URI) -> ANILIST
                path.contains(BuildConfig.SHIKI_REDIRECT_URI) -> SHIKIMORI
                else -> null
            }
        }
    }
}