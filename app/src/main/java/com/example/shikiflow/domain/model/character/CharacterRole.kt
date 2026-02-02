package com.example.shikiflow.domain.model.character

import com.example.shikiflow.R

enum class CharacterRole(val displayValue: Int) {
    MAIN(R.string.character_role_main),
    SUPPORTING(R.string.character_role_supporting),
    BACKGROUND(R.string.character_role_background),
    UNKNOWN(R.string.common_unknown)
}