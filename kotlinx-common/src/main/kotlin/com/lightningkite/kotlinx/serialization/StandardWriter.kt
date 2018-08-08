package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.collection.addSorted
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.json.KlaxonException
import kotlin.reflect.KClass

interface StandardWriter<OUT, RESULT> : AnyWriter<OUT, RESULT> {
    val writerGenerators: MutableList<Pair<Float, AnySubWriterGenerator<OUT, RESULT>>>

    val writers: MutableMap<KClass<*>, AnySubWriter<OUT, RESULT>>

    val boxWriter: OUT.(knownTypeInfo: KxType, value: Any?) -> RESULT

    override fun writer(type: KClass<*>): AnySubWriter<OUT, RESULT> = writers.getOrPut(type) {
        writerGenerators.asSequence().mapNotNull { it.second.invoke(type) }.firstOrNull()
                ?: throw KlaxonException("No writer available for type $type")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> setWriter(forType: KClass<T>, writer: OUT.(T?, KxType) -> RESULT) = writers.put(forType, writer as AnySubWriter<OUT, RESULT>)

    fun addWriterGenerator(priority: Float, writerGenerator: AnySubWriterGenerator<OUT, RESULT>) {
        writerGenerators.addSorted(priority to writerGenerator) { a, b -> a.first > b.first }
    }

    override fun write(type: KxType, value: Any?, to: OUT): RESULT = writer(type.base.kclass).invoke(to, value, type)
}