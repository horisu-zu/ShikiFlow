package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.type.MediaRelation
import com.example.graphql.shikimori.type.RelationKindEnum
import com.example.shikiflow.domain.model.track.RelationKind

object RelationKindMapper {
    fun RelationKindEnum.toDomain(): RelationKind {
        return when(this) {
            RelationKindEnum.adaptation -> RelationKind.ADAPTATION
            RelationKindEnum.alternative_setting -> RelationKind.ALTERNATIVE_SETTING
            RelationKindEnum.alternative_version -> RelationKind.ALTERNATIVE_VERSION
            RelationKindEnum.character -> RelationKind.CHARACTER
            RelationKindEnum.full_story -> RelationKind.FULL_STORY
            RelationKindEnum.other -> RelationKind.OTHER
            RelationKindEnum.parent_story -> RelationKind.SOURCE
            RelationKindEnum.prequel -> RelationKind.PREQUEL
            RelationKindEnum.sequel -> RelationKind.SEQUEL
            RelationKindEnum.side_story -> RelationKind.SIDE_STORY
            RelationKindEnum.spin_off -> RelationKind.SPIN_OFF
            RelationKindEnum.summary -> RelationKind.SUMMARY
            RelationKindEnum.UNKNOWN__ -> RelationKind.UNKNOWN
        }
    }

    fun MediaRelation.toDomain(): RelationKind {
        return when(this) {
            MediaRelation.ADAPTATION -> RelationKind.ADAPTATION
            MediaRelation.PREQUEL -> RelationKind.PREQUEL
            MediaRelation.SEQUEL -> RelationKind.SEQUEL
            MediaRelation.PARENT -> RelationKind.PARENT_STORY
            MediaRelation.SIDE_STORY -> RelationKind.SIDE_STORY
            MediaRelation.CHARACTER -> RelationKind.CHARACTER
            MediaRelation.SUMMARY -> RelationKind.SUMMARY
            MediaRelation.ALTERNATIVE -> RelationKind.ALTERNATIVE_VERSION
            MediaRelation.SPIN_OFF -> RelationKind.SPIN_OFF
            MediaRelation.OTHER -> RelationKind.OTHER
            MediaRelation.SOURCE -> RelationKind.SOURCE
            MediaRelation.COMPILATION -> RelationKind.COMPILATION
            MediaRelation.CONTAINS -> RelationKind.CONTAINS
            MediaRelation.UNKNOWN__ -> RelationKind.UNKNOWN
        }
    }
}