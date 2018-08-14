package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.collection.addSorted
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.json.KlaxonException
import kotlin.reflect.KClass

interface StandardReaderRepository<IN> : TypeReaderRepository<IN> {
    val readerGenerators: MutableList<Pair<Float, TypeReaderGenerator<IN>>>
    val readers: MutableMap<KClass<*>, TypeReader<IN>>

    override fun reader(type: KClass<*>): TypeReader<IN> = readers.getOrPut(type) {
        CommonSerialization.getDirectSubReader(this, type)
                ?: readerGenerators.asSequence().mapNotNull { it.second.invoke(type) }.firstOrNull()
                ?: throw KlaxonException("No reader available for type $type")
    }

    fun <T : Any> setReader(forType: KClass<T>, reader: IN.(KxType) -> T?) = readers.put(forType, reader)

    fun addReaderGenerator(priority: Float, readerGenerator: TypeReaderGenerator<IN>) {
        readerGenerators.addSorted(priority to readerGenerator) { a, b -> a.first > b.first }
    }

    fun read(type: KxType, from: IN): Any? = reader(type.base.kclass).invoke(from, type)
}