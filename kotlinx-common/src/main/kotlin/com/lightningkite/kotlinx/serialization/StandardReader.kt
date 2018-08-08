package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.collection.addSorted
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.json.KlaxonException
import kotlin.reflect.KClass

interface StandardReader<IN> : AnyReader<IN> {
    val readerGenerators: MutableList<Pair<Float, AnySubReaderGenerator<IN>>>

    val readers: MutableMap<KClass<*>, AnySubReader<IN>>

    val boxReader: IN.(knownTypeInfo: KxType) -> Any?

    override fun reader(type: KClass<*>): AnySubReader<IN> = readers.getOrPut(type) {
        readerGenerators.asSequence().mapNotNull { it.second.invoke(type) }.firstOrNull()
                ?: throw KlaxonException("No reader available for type $type")
    }

    fun <T : Any> setReader(forType: KClass<T>, reader: IN.(KxType) -> T?) = readers.put(forType, reader)

    fun addReaderGenerator(priority: Float, readerGenerator: AnySubReaderGenerator<IN>) {
        readerGenerators.addSorted(priority to readerGenerator) { a, b -> a.first > b.first }
    }

    override fun read(type: KxType, from: IN): Any? = reader(type.base.kclass).invoke(from, type)
}