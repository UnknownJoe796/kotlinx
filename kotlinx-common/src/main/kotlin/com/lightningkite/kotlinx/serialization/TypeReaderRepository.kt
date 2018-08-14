package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.KxType
import kotlin.reflect.KClass

interface TypeReaderRepository<IN> {
    fun reader(type: KClass<*>): TypeReader<IN>
}
typealias TypeReader<IN> = IN.(KxType) -> Any?
typealias TypeReaderGenerator<IN> = (KClass<*>) -> TypeReader<IN>?