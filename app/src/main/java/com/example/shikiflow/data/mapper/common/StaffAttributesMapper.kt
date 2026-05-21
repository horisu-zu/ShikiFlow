package com.example.shikiflow.data.mapper.common

import com.example.graphql.anilist.CharacterDetailsQuery
import com.example.graphql.anilist.StaffDetailsQuery
import com.example.shikiflow.data.mapper.common.DateMapper.toDomain
import com.example.shikiflow.domain.model.staff.Gender
import com.example.shikiflow.domain.model.staff.StaffAttributes

object StaffAttributesMapper {
    fun StaffDetailsQuery.Staff.attributes(): StaffAttributes {
        return StaffAttributes(
            dateOfBirth = dateOfBirth?.date?.toDomain(),
            dateOfDeath = dateOfDeath?.date?.toDomain(),
            age = age?.let { it.toString() },
            gender = gender?.toGenderEnum(),
            yearsActive = yearsActive?.mapNotNull { it }?.ifEmpty { null },
            hometown = homeTown
        )
    }

    fun CharacterDetailsQuery.Character.attributes(): StaffAttributes {
        return StaffAttributes(
            dateOfBirth = dateOfBirth?.date?.toDomain(),
            dateOfDeath = null,
            age = age,
            gender = gender?.toGenderEnum(),
            yearsActive = null,
            hometown = null
        )
    }

    private fun String.toGenderEnum(): Gender {
        return when(this) {
            "Male" -> Gender.MALE
            "Female" -> Gender.FEMALE
            else -> Gender.OTHER
        }
    }
}