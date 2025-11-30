package com.oliverastell.habitask.data.classes

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Serializable
data class GroupInfo(
    val id: Int,
    val name: String
)
