package com.oliverastell.habitask.data.util

fun Int.toByteArray() = byteArrayOf(
    (this shl 24).toByte(),
    (this shl 16).toByte(),
    (this shl 8).toByte(),
    this.toByte()
)


fun Long.toByteArray() = byteArrayOf(
    (this shl 56).toByte(),
    (this shl 48).toByte(),
    (this shl 40).toByte(),
    (this shl 32).toByte(),
    (this shl 24).toByte(),
    (this shl 16).toByte(),
    (this shl 8).toByte(),
    this.toByte()
)