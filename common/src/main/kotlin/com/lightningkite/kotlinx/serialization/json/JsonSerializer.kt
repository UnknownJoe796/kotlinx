package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.serialization.ExternalTypeRegistry
import com.lightningkite.kotlinx.serialization.Serializer
import com.lightningkite.kotlinx.serialization.externalName
import kotlin.reflect.KClass

class JsonSerializer : Serializer<Iterable<Char>> {

    private val readerGenerators = ArrayList<(KClass<*>) -> JsonTypeReader<Any>?>()
    private val writerGenerators = ArrayList<(KClass<*>) -> JsonTypeWriter<Any>?>()

    private val readers = HashMap<KClass<*>, JsonTypeReader<Any>>()
    private val writers = HashMap<KClass<*>, JsonTypeWriter<Any>>()

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

    fun <T : Any> setReader(forType: KClass<T>, reader: JsonTypeReader<T>) = readers.put(forType, reader)

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> setWriter(forType: KClass<T>, writer: JsonTypeWriter<T>) = writers.put(forType, writer as JsonWriter.(Any) -> Unit)


    inline fun <reified T : Any> JsonWriter.writeAny(item: T) = writeAny(item, T::class)
    fun <T : Any> JsonWriter.writeAny(item: T, type: KClass<T>) {
        getWriterUntyped(item::class).invoke(this, item)
    }

    inline fun <reified T : Any> JsonReader.readAny(): T? = readAny(T::class)
    fun <T : Any> JsonReader.readAny(type: KClass<T>): T? {
        if (lexer.peek().let { it.tokenType == TokenType.VALUE && it.value == null }) {
            return null
        }
        return getReader(type).invoke(this)
    }


    var boxWriter: JsonWriter.(KClass<*>, Any) -> Unit = { type, value ->
        writeArray {
            writeEntry {
                writeString(type.externalName!!)
            }
            writeEntry {
                @Suppress("UNCHECKED_CAST")
                writeAny(value, type as KClass<Any>)
            }
        }
    }
    var boxReader: JsonReader.() -> Any? = {
        if (lexer.peek().let { it.tokenType == TokenType.VALUE && it.value == null }) {
            null
        } else beginArray {
            val type = ExternalTypeRegistry[nextString()]
            @Suppress("UNCHECKED_CAST")
            readAny<Any>(type as KClass<Any>)
        }
    }


    init {
        setReader(Int::class) { nextInt() }
        setWriter(Int::class) { writeNumber(it) }

        setReader(Short::class) { nextInt().toShort() }
        setWriter(Short::class) { writeNumber(it) }

        setReader(Byte::class) { nextInt().toByte() }
        setWriter(Byte::class) { writeNumber(it) }

        setReader(Long::class) { nextLong() }
        setWriter(Long::class) { writeNumber(it) }

        setReader(Float::class) { nextDouble().toFloat() }
        setWriter(Float::class) { writeNumber(it) }

        setReader(Double::class) { nextDouble() }
        setWriter(Double::class) { writeNumber(it) }

        setReader(Boolean::class) { nextBoolean() }
        setWriter(Boolean::class) { writeBoolean(it) }

        setReader(String::class) { nextString() }
        setWriter(String::class) { writeString(it) }

        setReader()
    }
}