package com.example.shikiflow.data.mapper

import com.example.shikiflow.di.annotations.GraphQLScalar

@GraphQLScalar("AnimeKind")
enum class AnimeKind {
    MOVIE,
    MUSIC,
    ONA,
    OVA_ONA,
    OVA,
    SPECIAL,
    TV,
    TV_13,
    TV_24,
    TV_48,
    TV_SPECIAL,
    PV,
    CM
}