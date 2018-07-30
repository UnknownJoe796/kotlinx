package com.lightningkite.kotlinx.serialization

import kotlin.reflect.KClass

interface Serializer<SER> {
    fun read(value: SER): Any?
    fun write(value: Any?): SER
    fun <T: Any> readType(type: KClass<T>, value: SER): T?
    fun <T: Any> writeType(type: KClass<T>, value: T?): SER
}

