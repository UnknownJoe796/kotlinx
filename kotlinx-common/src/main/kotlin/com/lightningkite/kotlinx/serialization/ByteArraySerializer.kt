package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.KxType

interface ByteArraySerializer {
    val contentType: String
    fun write(value: Any?, type: KxType): ByteArray
    fun read(from: ByteArray, type: KxType): Any?
}