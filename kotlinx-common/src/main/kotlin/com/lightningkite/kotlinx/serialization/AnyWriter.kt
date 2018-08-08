package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.KxType
import kotlin.reflect.KClass

interface AnyWriter<OUT, RESULT> {
    fun write(type: KxType, value: Any?, to: OUT): RESULT
    fun writer(type: KClass<*>): AnySubWriter<OUT, RESULT>
}

typealias AnySubWriter<OUT, RESULT> = OUT.(Any?, KxType) -> RESULT
typealias AnySubWriterGenerator<OUT, RESULT> = (KClass<*>) -> AnySubWriter<OUT, RESULT>?