package com.lightningkite.kotlinx.serialization

import kotlin.reflect.KClass

object CommonSerialization {
    val defaultReaders = HashMap<KClass<*>, (AnyReader<Any?>) -> AnySubReader<Any?>>()
    val defaultWriters = HashMap<KClass<*>, (AnyWriter<Any?, Any?>) -> AnySubWriter<Any?, Any?>>()
    val defaultReaderGenerators = ArrayList<Pair<Float, (AnyReader<Any?>, type: KClass<*>) -> AnySubReader<Any?>?>>()
    val defaultWriterGenerators = ArrayList<Pair<Float, (AnyWriter<Any?, Any?>, type: KClass<*>) -> AnySubWriter<Any?, Any?>?>>()


    @Suppress("UNCHECKED_CAST")
    fun <IN> getDirectSubReader(
            anyReader: AnyReader<IN>,
            type: KClass<*>
    ): AnySubReader<IN>? = defaultReaders[type]?.invoke(anyReader as AnyReader<Any?>)

    @Suppress("UNCHECKED_CAST")
    fun <OUT, RESULT> getDirectSubWriter(
            anyWriter: AnyWriter<OUT, RESULT>,
            type: KClass<*>
    ): AnySubWriter<OUT, RESULT>? = defaultWriters[type]?.invoke(anyWriter as AnyWriter<Any?, Any?>) as? AnySubWriter<OUT, RESULT>
}