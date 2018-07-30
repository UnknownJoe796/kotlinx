package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.serialization.Serializer
import kotlin.reflect.KClass

class JsonSerializer : Serializer<Iterable<Char>> {

    @Suppress("UNCHECKED_CAST")
    fun JsonWriter.writeAny(item: Any?) {
        if(item == null)
            writeNull()
        else
            writeAny<Any>(item, item::class as KClass<Any>)
    }
    fun <T: Any> JsonWriter.writeAny(item: T, type: KClass<T>) {
        getWriterUntyped(item::class).invoke(this, item)
    }

    fun <T: Any> JsonReader.readAny(type: KClass<T>):T? {
        if(lexer.peek().let{ it.tokenType == TokenType.VALUE && it.value == null }){
            return null
        }
        return getReader(type).invoke(this)
    }

    private val readerGenerators = ArrayList<(KClass<*>) -> (JsonReader.() -> Any)?>()
    private val writerGenerators = ArrayList<(KClass<*>) -> (JsonWriter.(Any) -> Unit)?>()

    private val readers = HashMap<KClass<*>, JsonReader.() -> Any>()
    private val writers = HashMap<KClass<*>, JsonWriter.(Any) -> Unit>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getReader(forType: KClass<T>) = readers.getOrPut(forType) {
        readerGenerators.asSequence().mapNotNull { it.invoke(forType) }.firstOrNull()
                ?: throw IllegalArgumentException("No generator available")
    } as JsonReader.() -> T

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getWriter(forType: KClass<T>) = writers.getOrPut(forType) {
        writerGenerators.asSequence().mapNotNull { it.invoke(forType) }.firstOrNull()
                ?: throw IllegalArgumentException("No generator available")
    } as JsonWriter.(T) -> Unit

    @Suppress("UNCHECKED_CAST")
    fun getReaderUntyped(forType: KClass<*>) = readers.getOrPut(forType) {
        readerGenerators.asSequence().mapNotNull { it.invoke(forType) }.firstOrNull()
                ?: throw IllegalArgumentException("No generator available")
    } as JsonReader.() -> Any?

    @Suppress("UNCHECKED_CAST")
    fun getWriterUntyped(forType: KClass<*>) = writers.getOrPut(forType) {
        writerGenerators.asSequence().mapNotNull { it.invoke(forType) }.firstOrNull()
                ?: throw IllegalArgumentException("No generator available")
    } as JsonWriter.(Any?) -> Unit

    fun <T : Any> setReader(forType: KClass<T>, reader: JsonReader.() -> T) = readers.put(forType, reader)

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> setWriter(forType: KClass<T>, writer: JsonWriter.(T) -> Unit) = writers.put(forType, writer as JsonWriter.(Any) -> Unit)
}