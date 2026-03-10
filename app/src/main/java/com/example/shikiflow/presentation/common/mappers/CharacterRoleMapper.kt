package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.character.CharacterRole

object CharacterRoleMapper {
    fun CharacterRole.displayValue(): Int {
        return when(this) {
            CharacterRole.MAIN -> R.string.character_role_main
            CharacterRole.SUPPORTING -> R.string.character_role_supporting
            CharacterRole.BACKGROUND -> R.string.character_role_background
            CharacterRole.UNKNOWN -> R.string.common_unknown
        }
    }
}