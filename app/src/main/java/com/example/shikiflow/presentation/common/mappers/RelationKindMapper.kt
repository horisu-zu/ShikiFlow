package com.example.shikiflow.presentation.common.mappers

import com.example.shikiflow.R
import com.example.shikiflow.domain.model.track.RelationKind

object RelationKindMapper {
    fun RelationKind.displayValue(): Int {
        return when(this) {
            RelationKind.ADAPTATION -> R.string.relation_kind_adaptation
            RelationKind.ALTERNATIVE_SETTING -> R.string.relation_kind_alternative_setting
            RelationKind.ALTERNATIVE_VERSION -> R.string.relation_kind_alternative_version
            RelationKind.CHARACTER -> R.string.relation_kind_character
            RelationKind.FULL_STORY -> R.string.relation_kind_full_story
            RelationKind.OTHER -> R.string.relation_kind_other
            RelationKind.PARENT_STORY, RelationKind.SOURCE -> R.string.relation_kind_source
            RelationKind.PREQUEL -> R.string.relation_kind_prequel
            RelationKind.SEQUEL -> R.string.relation_kind_sequel
            RelationKind.SIDE_STORY -> R.string.relation_kind_side_story
            RelationKind.SPIN_OFF -> R.string.relation_kind_spin_off
            RelationKind.SUMMARY -> R.string.relation_kind_summary
            RelationKind.COMPILATION -> R.string.relation_kind_compilation
            RelationKind.CONTAINS -> R.string.relation_kind_contains
            else -> R.string.common_unknown
        }
    }
}