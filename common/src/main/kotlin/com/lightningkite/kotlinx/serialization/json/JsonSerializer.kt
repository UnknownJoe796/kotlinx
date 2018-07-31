package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.locale.Date
import com.lightningkite.kotlinx.locale.DateTime
import com.lightningkite.kotlinx.locale.Time
import com.lightningkite.kotlinx.locale.TimeStamp
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.ExternalTypeRegistry
import com.lightningkite.kotlinx.serialization.externalName
import kotlin.reflect.KClass

class JsonSerializer {

    private val readerGenerators = ArrayList<(KClass<*>) -> JsonTypeReader<Any>?>()
    private val writerGenerators = ArrayList<(KClass<*>) -> JsonTypeWriter<Any>?>()

    private val readers = HashMap<KClass<*>, JsonTypeReader<Any>>()
    private val writers = HashMap<KClass<*>, JsonTypeWriter<Any>>()

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getReader(forType: KClass<T>) = readers.getOrPut(forType) {
        readerGenerators.asSequence().mapNotNull { it.invoke(forType) }.firstOrNull()
                ?: throw IllegalArgumentException("No reader available for type $forType")
    } as JsonTypeReader<T>

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getWriter(forType: KClass<T>) = writers.getOrPut(forType) {
        writerGenerators.asSequence().mapNotNull { it.invoke(forType) }.firstOrNull()
                ?: throw IllegalArgumentException("No writer available for type $forType")
    } as JsonTypeWriter<T>

    @Suppress("UNCHECKED_CAST")
    fun getReaderUntyped(forType: KClass<*>) = readers.getOrPut(forType) {
        readerGenerators.asSequence().mapNotNull { it.invoke(forType) }.firstOrNull()
                ?: throw IllegalArgumentException("No reader available for type $forType")
    }

    @Suppress("UNCHECKED_CAST")
    fun getWriterUntyped(forType: KClass<*>) = writers.getOrPut(forType) {
        writerGenerators.asSequence().mapNotNull { it.invoke(forType) }.firstOrNull()
                ?: throw IllegalArgumentException("No writer available for type $forType")
    }

    fun <T : Any> setReader(forType: KClass<T>, reader: JsonTypeReader<T>) = readers.put(forType, reader)

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> setWriter(forType: KClass<T>, writer: JsonTypeWriter<T>) = writers.put(forType, writer as JsonTypeWriter<Any>)


    var boxWriter: JsonWriter.(KClass<*>, typeInfo: KxType?, Any) -> Unit = { _, typeInfo, value ->
        writeArray {
            val useType = when (value) {
                is List<*> -> List::class
                is Map<*, *> -> Map::class
                else -> value::class
            }
            writeEntry {
                writeString(useType.externalName!!)
            }
            writeEntry {
                @Suppress("UNCHECKED_CAST")
                writeAny(value, null, useType as KClass<Any>)
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


    fun <T : Any> read(type: KClass<T>, additionalTypeInformation: KxType? = null, source: Iterator<Char>): T? {
        return JsonReader(source).readAny(type, additionalTypeInformation)
    }

    fun <T : Any> read(type: KClass<T>, additionalTypeInformation: KxType? = null, source: Iterable<Char>): T? {
        return JsonReader(source.iterator()).readAny(type, additionalTypeInformation)
    }

    fun <T : Any> read(type: KClass<T>, additionalTypeInformation: KxType? = null, source: CharSequence): T? {
        return JsonReader(source.iterator()).readAny(type, additionalTypeInformation)
    }

    fun <T : Any> write(type: KClass<T>, additionalTypeInformation: KxType? = null, value: T?): StringBuilder {
        return StringBuilder().json {
            if (value == null) writeNull()
            else writeAny(value, additionalTypeInformation, type)
        }
    }


    inline fun <reified T : Any> JsonWriter.writeAny(item: T) = writeAny(item, null, T::class)

    @Suppress("NOTHING_TO_INLINE")
    inline fun <T : Any> JsonWriter.writeAny(item: T, additionalTypeInformation: KxType) = writeAny(item, additionalTypeInformation, additionalTypeInformation.base.kclass as KClass<T>)

    fun <T : Any> JsonWriter.writeAny(item: T, additionalTypeInformation: KxType? = null, type: KClass<T>) {
        getWriterUntyped(type).invoke(this, additionalTypeInformation, item)
    }

    inline fun <reified T : Any> JsonReader.readAny(): T? = readAny(T::class)
    @Suppress("NOTHING_TO_INLINE")
    inline fun <T : Any> JsonReader.readAny(additionalTypeInformation: KxType): T? = readAny(additionalTypeInformation.base.kclass as KClass<T>, additionalTypeInformation)

    fun <T : Any> JsonReader.readAny(type: KClass<T>, additionalTypeInformation: KxType? = null): T? {
        if (lexer.peek().let { it.tokenType == TokenType.VALUE && it.value == null }) {
            return null
        }
        return getReader(type).invoke(this, additionalTypeInformation)
    }


    fun <T : Any> polyboxWriter(forType: KClass<T>): JsonTypeWriter<T> = { t, value -> boxWriter.invoke(this, forType, t, value) }
    fun <T : Any> polyboxReader(forType: KClass<T>): JsonTypeReader<T> = {
        @Suppress("UNCHECKED_CAST")
        boxReader.invoke(this) as T
    }

    init {
        setReader(Unit::class) { nextAny(); Unit }
        setWriter(Unit::class) { _, it -> writeNull() }

        setReader(Int::class) { nextInt() }
        setWriter(Int::class) { _, it -> writeNumber(it) }

        setReader(Short::class) { nextInt().toShort() }
        setWriter(Short::class) { _, it -> writeNumber(it) }

        setReader(Byte::class) { nextInt().toByte() }
        setWriter(Byte::class) { _, it -> writeNumber(it) }

        setReader(Long::class) { nextLong() }
        setWriter(Long::class) { _, it -> writeNumber(it) }

        setReader(Float::class) { nextDouble().toFloat() }
        setWriter(Float::class) { _, it -> writeNumber(it) }

        setReader(Double::class) { nextDouble() }
        setWriter(Double::class) { _, it -> writeNumber(it) }

        setReader(Number::class) { nextDouble() }
        setWriter(Number::class) { _, it -> writeNumber(it) }

        setReader(Boolean::class) { nextBoolean() }
        setWriter(Boolean::class) { _, it -> writeBoolean(it) }

        setReader(String::class) { nextString() }
        setWriter(String::class) { _, it -> writeString(it) }

        setReader(Date::class) { Date(nextInt()) }
        setWriter(Date::class) { _, it -> writeNumber(it.daysSinceEpoch) }

        setReader(Time::class) { Time(nextInt()) }
        setWriter(Time::class) { _, it -> writeNumber(it.millisecondsSinceMidnight) }

        setReader(DateTime::class) {
            beginArray {
                DateTime(Date(nextInt()), Time(nextInt()))
            }
        }
        setWriter(DateTime::class) { _, it ->
            writeArray {
                writeEntry {
                    writeNumber(it.date.daysSinceEpoch)
                }
                writeEntry {
                    writeNumber(it.time.millisecondsSinceMidnight)
                }
            }
        }

        setReader(TimeStamp::class) { TimeStamp(nextLong()) }
        setWriter(TimeStamp::class) { _, it -> writeNumber(it.millisecondsSinceEpoch) }

        setReader(List::class, ListSerializer.reader(this))
        setWriter(List::class, ListSerializer.writer(this))

        setReader(Map::class, MapSerializer.reader(this))
        setWriter(Map::class, MapSerializer.writer(this))

        setReader(Any::class, polyboxReader(Any::class))
        setWriter(Any::class, polyboxWriter(Any::class))

        writerGenerators += EnumGenerators.writer
        readerGenerators += EnumGenerators.reader

        writerGenerators += ReflectionGenerators.writerGenerator(this)
        readerGenerators += ReflectionGenerators.readerGeneratorNoArg(this)
        readerGenerators += ReflectionGenerators.readerGeneratorAnyConstructor(this)
    }
}