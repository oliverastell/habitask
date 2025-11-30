package com.oliverastell.habitask.data.classes

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: Int,
    val name: String,
    val groupId: Int,
    val lastOnline: Long,
)