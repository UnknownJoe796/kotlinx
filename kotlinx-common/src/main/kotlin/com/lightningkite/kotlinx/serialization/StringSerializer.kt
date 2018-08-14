package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.KxType

interface StringSerializer {
    val contentType: String
    fun write(value: Any?, type: KxType): String
    fun read(from: String, type: KxType): Any?
}
