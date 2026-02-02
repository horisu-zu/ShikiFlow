package com.example.shikiflow.data.datasource.dto.person

import com.example.shikiflow.data.datasource.dto.person.GroupedRolesSerializer
import com.example.shikiflow.data.datasource.dto.ShikiImage
import com.example.shikiflow.data.datasource.dto.ShikiImageSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ShikiPerson(
    @SerialName("groupped_roles")
    @Serializable(with = GroupedRolesSerializer::class)
    val groupedRoles: List<GroupedRole>?,
    val id: Int,
    @Serializable(with = ShikiImageSerializer::class)
    val image: ShikiImage,
    val japanese: String?,
    @SerialName("birth_on") val birthDate: ShikiDate? = null,
    @SerialName("job_title")
    val jobTitle: String?,
    val name: String,
    val roles: List<Role>?,
    val seyu: Boolean?,
    @SerialName("topic_id")
    val topicId: Int?,
    val works: List<Work>?
)

@Serializable
data class ShikiDate(
    val day: Int,
    val month: Int,
    val year: Int
)