package com.example.shikiflow.domain.model.person

import com.example.shikiflow.domain.model.common.GroupedRolesSerializer
import com.example.shikiflow.domain.model.common.ShikiImage
import com.example.shikiflow.domain.model.common.ShikiImageSerializer
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
    @SerialName("job_title")
    val jobTitle: String?,
    val name: String,
    val roles: List<Role>?,
    val seyu: Boolean?,
    @SerialName("topic_id")
    val topicId: Int?,
    val works: List<Work>?
)