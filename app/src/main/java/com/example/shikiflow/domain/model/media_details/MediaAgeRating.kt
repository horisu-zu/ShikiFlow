package com.example.shikiflow.domain.model.media_details

import com.example.shikiflow.R

enum class MediaAgeRating(val displayValue: Int) {
    RX(R.string.rating_rx),
    R_PLUS(R.string.rating_r_plus),
    R_17(R.string.rating_r_17),
    PG_13(R.string.rating_pg_13),
    PG(R.string.rating_pg),
    G(R.string.rating_g),
    NONE(R.string.rating_none),
    UNKNOWN(R.string.common_unknown)
}
