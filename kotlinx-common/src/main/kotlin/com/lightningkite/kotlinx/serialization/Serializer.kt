package com.lightningkite.kotlinx.serialization

import kotlin.reflect.KClass

interface Serializer<IN> {
    fun <T: Any> read(type: KClass<T>, value: IN): T?
}

interface Deserializer<OUT> {
    fun <T: Any> write(type: KClass<T>, value: T?): OUT
}