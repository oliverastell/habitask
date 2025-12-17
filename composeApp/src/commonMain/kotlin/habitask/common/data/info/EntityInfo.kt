package habitask.common.data.info

import kotlinx.serialization.Serializable

@Serializable
enum class EntityType {
    User,
    Group,
    Folder
}

@Serializable
data class EntityInfo(
    val id: Int,
    val name: String,
    val parent: Int?,
    val entityType: EntityType
)