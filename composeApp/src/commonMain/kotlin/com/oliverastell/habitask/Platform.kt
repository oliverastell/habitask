package com.oliverastell.habitask

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform