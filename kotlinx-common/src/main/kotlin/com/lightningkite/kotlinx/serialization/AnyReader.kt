package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.KxType
import kotlin.reflect.KClass

interface AnyReader<IN> {
    fun read(type: KxType, from: IN): Any?
    fun reader(type: KClass<*>): AnySubReader<IN> = { read(it, this) }
}
typealias AnySubReader<IN> = IN.(KxType) -> Any?
typealias AnySubReaderGenerator<IN> = (KClass<*>) -> AnySubReader<IN>?