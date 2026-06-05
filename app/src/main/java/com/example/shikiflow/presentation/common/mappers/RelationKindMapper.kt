package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.RelationKind

object RelationKindMapper {
    fun RelationKind.displayValue(): Int {
        return when(this) {
            RelationKind.ADAPTATION -> R.string.relation_format_adaptation
            RelationKind.ALTERNATIVE_SETTING -> R.string.relation_format_alternative_setting
            RelationKind.ALTERNATIVE_VERSION -> R.string.relation_format_alternative_version
            RelationKind.CHARACTER -> R.string.relation_format_character
            RelationKind.FULL_STORY -> R.string.relation_format_full_story
            RelationKind.OTHER -> R.string.relation_format_other
            RelationKind.PARENT_STORY, RelationKind.SOURCE -> R.string.relation_format_source
            RelationKind.PREQUEL -> R.string.relation_format_prequel
            RelationKind.SEQUEL -> R.string.relation_format_sequel
            RelationKind.SIDE_STORY -> R.string.relation_format_side_story
            RelationKind.SPIN_OFF -> R.string.relation_format_spin_off
            RelationKind.SUMMARY -> R.string.relation_format_summary
            RelationKind.COMPILATION -> R.string.relation_format_compilation
            RelationKind.CONTAINS -> R.string.relation_format_contains
            else -> R.string.common_unknown
        }
    }
}