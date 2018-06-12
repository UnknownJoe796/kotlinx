package com.lightningkite.kotlinx.serialization

interface Serializer<SER> {
    fun read(value: SER): Any?
    fun write(value: Any?): SER
}

