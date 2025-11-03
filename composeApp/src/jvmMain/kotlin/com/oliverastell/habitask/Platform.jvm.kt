package com.oliverastell.habitask

class NativePlatform: Platform {
    override val name: String = "Java ${Runtime.version()}"
}

actual fun getPlatform(): Platform = NativePlatform()