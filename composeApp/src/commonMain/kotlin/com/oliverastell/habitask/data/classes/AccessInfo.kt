package com.oliverastell.habitask.data.classes

import kotlinx.serialization.Serializable

@Serializable
data class AccessInfo(
    val token: String,
    val userId: Int
)