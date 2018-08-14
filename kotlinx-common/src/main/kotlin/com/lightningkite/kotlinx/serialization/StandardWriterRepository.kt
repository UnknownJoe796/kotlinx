package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.collection.addSorted
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.json.KlaxonException
import kotlin.reflect.KClass

interface StandardWriterRepository<OUT, RESULT> : TypeWriterRepository<OUT, RESULT> {
    val writerGenerators: MutableList<Pair<Float, TypeWriterGenerator<OUT, RESULT>>>

    val writers: MutableMap<KClass<*>, TypeWriter<OUT, RESULT>>

    override fun writer(type: KClass<*>): TypeWriter<OUT, RESULT> = writers.getOrPut(type) {
        CommonSerialization.getDirectSubWriter(this, type)
                ?: writerGenerators.asSequence().mapNotNull { it.second.invoke(type) }.firstOrNull()
                ?: throw KlaxonException("No writer available for type $type")
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> setWriter(forType: KClass<T>, writer: OUT.(T?, KxType) -> RESULT) = writers.put(forType, writer as TypeWriter<OUT, RESULT>)

    fun addWriterGenerator(priority: Float, writerGenerator: TypeWriterGenerator<OUT, RESULT>) {
        writerGenerators.addSorted(priority to writerGenerator) { a, b -> a.first > b.first }
    }

    fun write(type: KxType, value: Any?, to: OUT): RESULT = writer(type.base.kclass).invoke(to, value, type)
}