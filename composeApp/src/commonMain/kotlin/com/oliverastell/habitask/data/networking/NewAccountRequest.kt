package com.oliverastell.habitask.data.networking

import kotlinx.serialization.Serializable

@Serializable
data class NewAccountRequest(
    val name: String
)