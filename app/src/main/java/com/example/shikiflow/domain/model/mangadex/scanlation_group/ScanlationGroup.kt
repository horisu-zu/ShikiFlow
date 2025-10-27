package com.example.shikiflow.domain.model.mangadex.scanlation_group

data class ScanlationGroup(
    val id: String,
    val name: String,
    val isOfficial: Boolean,
    val website: String?
) {
    companion object {
        fun GroupData.toDomain(): ScanlationGroup {
            return ScanlationGroup(
                id = this.id,
                name = this.attributes.name,
                isOfficial = this.attributes.official,
                website = this.attributes.website
            )
        }
    }
}
