package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.KxType
import kotlin.reflect.KClass

interface TypeWriterRepository<OUT, RESULT> {
    fun writer(type: KClass<*>): TypeWriter<OUT, RESULT>
}

typealias TypeWriter<OUT, RESULT> = OUT.(Any?, KxType) -> RESULT
typealias TypeWriterGenerator<OUT, RESULT> = (KClass<*>) -> TypeWriter<OUT, RESULT>?

/*

TypeWriterRepository - Something that gives writers for KClasses
TypeWriter - Something that writes a particular KClass
WritesTo - Something that can write anything to a particular format

*/