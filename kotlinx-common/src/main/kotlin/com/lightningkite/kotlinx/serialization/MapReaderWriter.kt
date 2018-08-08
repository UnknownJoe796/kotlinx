package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.AnyReflection
import com.lightningkite.kotlinx.reflection.KxType

object MapReaderWriter {

    inline fun <OUT, RESULT> writer(
            forWriter: AnyWriter<OUT, RESULT>,
            crossinline writeObject: OUT.(
                    keySubtype: KxType,
                    action: (
                            writeField: (
                                    Any?,
                                    OUT.() -> RESULT
                            ) -> Unit
                    ) -> Unit
            ) -> RESULT
    ): AnySubWriter<OUT, RESULT> = { value, typeInfo ->
        val keySubtype = typeInfo.typeParameters.getOrNull(0)?.takeUnless { it.isStar }?.type
                ?: KxType(AnyReflection, true)
        val valueSubtype = typeInfo.typeParameters.getOrNull(1)?.takeUnless { it.isStar }?.type
                ?: KxType(AnyReflection, true)
        val valueSubtypeWriter = forWriter.writer(valueSubtype.base.kclass)
        writeObject(keySubtype) { writeField ->
            @Suppress("UNCHECKED_CAST")
            for (entry in value as Map<Any?, Any?>) {
                writeField(entry.key) {
                    valueSubtypeWriter.invoke(this, entry.value, valueSubtype)
                }
            }
        }
    }


    inline fun <IN> reader(
            forReader: AnyReader<IN>,
            crossinline readObject: IN.(
                    keyType: KxType,
                    onField: IN.(Any?) -> Unit
            ) -> Unit
    ): AnySubReader<IN> = { typeInfo ->
        val keySubtype = typeInfo.typeParameters.getOrNull(0)?.takeUnless { it.isStar }?.type
                ?: KxType(AnyReflection, true)
        val valueSubtype = typeInfo.typeParameters.getOrNull(1)?.takeUnless { it.isStar }?.type
                ?: KxType(AnyReflection, true)
        val output = LinkedHashMap<Any?, Any?>()
        val valueSubtypeReader = forReader.reader(valueSubtype.base.kclass)
        readObject(keySubtype) { name ->
            output[name] = valueSubtypeReader.invoke(this, valueSubtype)
        }
        output
    }
}