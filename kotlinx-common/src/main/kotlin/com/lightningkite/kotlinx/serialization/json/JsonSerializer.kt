package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.locale.Date
import com.lightningkite.kotlinx.locale.DateTime
import com.lightningkite.kotlinx.locale.Time
import com.lightningkite.kotlinx.locale.TimeStamp
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.StringReflection
import com.lightningkite.kotlinx.reflection.kxType
import com.lightningkite.kotlinx.serialization.*
import kotlin.reflect.KClass

@Suppress("LeakingThis")
open class JsonSerializer : StandardReader<RawJsonReader>, StandardWriter<RawJsonWriter, Unit> {

    companion object : JsonSerializer()

    override val readerGenerators: MutableList<Pair<Float, AnySubReaderGenerator<RawJsonReader>>> = ArrayList()
    override val readers: MutableMap<KClass<*>, AnySubReader<RawJsonReader>> = HashMap()
    override val writerGenerators: MutableList<Pair<Float, AnySubWriterGenerator<RawJsonWriter, Unit>>> = ArrayList()
    override val writers: MutableMap<KClass<*>, AnySubWriter<RawJsonWriter, Unit>> = HashMap()

    override var boxWriter: RawJsonWriter.(typeInfo: KxType, Any?) -> Unit = { typeInfo, value ->
        if (value == null) writeNull()
        else writeArray {
            val useType = when (value) {
                is List<*> -> List::class
                is Map<*, *> -> Map::class
                else -> value::class
            }
            writeEntry {
                writeString(useType.externalName!!)
            }
            writeEntry {
                writer(useType).invoke(this, value, typeInfo)
            }
        }
    }
    override var boxReader: RawJsonReader.(typeInfo: KxType) -> Any? = {
        if (lexer.peek().let { it.tokenType == TokenType.VALUE && it.value == null }) {
            null
        } else beginArray {
            val type = ExternalTypeRegistry[nextString()]!!
            reader(type).invoke(this, type.kxType)
        }
    }


    fun polyboxWriter(forType: KClass<*>): AnySubWriter<RawJsonWriter, Unit> = { value, t -> boxWriter.invoke(this, t, value) }
    fun polyboxReader(forType: KClass<*>): AnySubReader<RawJsonReader> = {
        @Suppress("UNCHECKED_CAST")
        boxReader.invoke(this, it)
    }

    inline fun <T : Any> setNullableReader(typeKClass: KClass<T>, crossinline read: RawJsonReader.(KxType) -> T) {
        setReader(typeKClass) {
            if (lexer.peek().let { it.tokenType == TokenType.VALUE && it.value == null })
                null
            else
                read(it)
        }
    }

    inline fun <T : Any> setNullableWriter(typeKClass: KClass<T>, crossinline write: RawJsonWriter.(T, KxType) -> Unit) {
        setWriter(typeKClass) { it, type ->
            if (it == null) writeNull()
            else write(it, type)
        }
    }

    inline fun setNullableReaderRaw(typeKClass: KClass<*>, crossinline read: AnySubReader<RawJsonReader>) {
        readers[typeKClass] = {
            if (lexer.peek().let { it.tokenType == TokenType.VALUE && it.value == null })
                null
            else
                read(it)
        }
    }

    inline fun setNullableWriterRaw(typeKClass: KClass<*>, crossinline write: AnySubWriter<RawJsonWriter, Unit>) {
        writers[typeKClass] = { it, type ->
            if (it == null) writeNull()
            else write(it, type)
        }
    }

    init {
        setNullableReader(Unit::class) { nextAny(); Unit }
        setNullableWriter(Unit::class) { it, _ -> writeNull() }

        setNullableReader(Int::class) { nextInt() }
        setNullableWriter(Int::class) { it, _ -> writeNumber(it) }

        setNullableReader(Short::class) { nextInt().toShort() }
        setNullableWriter(Short::class) { it, _ -> writeNumber(it) }

        setNullableReader(Byte::class) { nextInt().toByte() }
        setNullableWriter(Byte::class) { it, _ -> writeNumber(it) }

        setNullableReader(Long::class) { nextLong() }
        setNullableWriter(Long::class) { it, _ -> writeNumber(it) }

        setNullableReader(Float::class) { nextDouble().toFloat() }
        setNullableWriter(Float::class) { it, _ -> writeNumber(it) }

        setNullableReader(Double::class) { nextDouble() }
        setNullableWriter(Double::class) { it, _ -> writeNumber(it) }

        setNullableReader(Number::class) { nextDouble() }
        setNullableWriter(Number::class) { it, _ -> writeNumber(it) }

        setNullableReader(Boolean::class) { nextBoolean() }
        setNullableWriter(Boolean::class) { it, _ -> writeBoolean(it) }

        setNullableReader(String::class) { nextString() }
        setNullableWriter(String::class) { it, _ -> writeString(it) }

        setNullableReader(Date::class) { Date(nextInt()) }
        setNullableWriter(Date::class) { it, _ -> writeNumber(it.daysSinceEpoch) }

        setNullableReader(Time::class) { Time(nextInt()) }
        setNullableWriter(Time::class) { it, _ -> writeNumber(it.millisecondsSinceMidnight) }

        setNullableReader(DateTime::class) {
            beginArray {
                DateTime(Date(nextInt()), Time(nextInt()))
            }
        }
        setNullableWriter(DateTime::class) { it, _ ->
            writeArray {
                writeEntry {
                    writeNumber(it.date.daysSinceEpoch)
                }
                writeEntry {
                    writeNumber(it.time.millisecondsSinceMidnight)
                }
            }
        }

        setNullableReader(TimeStamp::class) { TimeStamp(nextLong()) }
        setNullableWriter(TimeStamp::class) { it, _ -> writeNumber(it.millisecondsSinceEpoch) }

        setNullableReaderRaw(List::class, ListReaderWriter.reader(
                forReader = this,
                readList = {
                    beginArray {
                        while (hasNext()) {
                            it.invoke(this)
                        }
                    }
                }
        ))
        setNullableWriterRaw(List::class, ListReaderWriter.writer<RawJsonWriter, Unit, RawJsonWriter.ArrayWriter>(
                forWriter = this,
                writeList = { writeArray(it) },
                writeEntry = { writeEntry(it) }
        ))

        setNullableReaderRaw(Map::class, MapReaderWriter.reader(
                forReader = this,
                readObject = { keyType, onField ->
                    if (keyType.base == StringReflection) {
                        beginObject {
                            while (hasNext()) {
                                onField.invoke(this, nextName())
                            }
                        }
                    } else {
                        val reader = reader(keyType.base.kclass)
                        beginObject {
                            while (hasNext()) {
                                val key = reader.invoke(RawJsonReader(nextName().iterator()), keyType)
                                onField.invoke(this, key)
                            }
                        }
                    }
                }
        ))
        setNullableWriterRaw(Map::class, MapReaderWriter.writer(
                forWriter = this,
                writeObject = { keyType, action ->
                    if (keyType.base == StringReflection) {
                        writeObject {
                            action.invoke { key, valueWrite ->
                                writeEntry(key as String, valueWrite)
                            }
                        }
                    } else {
                        val keyWriter = writer(keyType.base.kclass)
                        writeObject {
                            action.invoke { key, valueWrite ->
                                val stringifiedKey = StringBuilder().also { it ->
                                    RawJsonWriter(it).let {
                                        keyWriter.invoke(it, key, keyType)
                                    }
                                }.toString()
                                writeEntry(stringifiedKey, valueWrite)
                            }
                        }
                    }
                }
        ))

//        setNullableReader(List::class, ListSerializer.reader(this))
//        setNullableWriter(List::class, ListSerializer.writer(this))
//
//        setNullableReader(Map::class, MapSerializer.reader(this))
//        setNullableWriter(Map::class, MapSerializer.writer(this))

        setReader(Any::class, polyboxReader(Any::class))
        setWriter(Any::class, polyboxWriter(Any::class))

        addWriterGenerator(1f, EnumGenerators.writerGenerator(this))
        addReaderGenerator(1f, EnumGenerators.readerGenerator(this))

        writerGenerators += ReflectionGenerators.writerGenerator(this)
        readerGenerators += ReflectionGenerators.readerGeneratorNoArg(this)
        readerGenerators += ReflectionGenerators.readerGeneratorAnyConstructor(this)
    }
}