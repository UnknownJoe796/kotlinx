package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.AnyReflection
import com.lightningkite.kotlinx.reflection.KxType

object ListReaderWriter {

    inline fun <OUT, RESULT> writer(
            forWriter: AnyWriter<OUT, RESULT>,
            crossinline writeList: OUT.(
                    action: (
                            writeEntry: (
                                    OUT.() -> RESULT
                            ) -> Unit
                    ) -> Unit
            ) -> RESULT
    ): AnySubWriter<OUT, RESULT> = { value, typeInfo ->
        val valueSubtype = typeInfo.typeParameters.getOrNull(0)?.takeUnless { it.isStar }?.type
                ?: KxType(AnyReflection, true)
        val valueSubtypeWriter = forWriter.writer(valueSubtype.base.kclass)
        writeList { writeEntry ->
            @Suppress("UNCHECKED_CAST")
            for (subvalue in value as List<Any?>) {
                writeEntry {
                    valueSubtypeWriter.invoke(this, subvalue, valueSubtype)
                }
            }
        }
    }

    inline fun <IN> reader(
            forReader: AnyReader<IN>,
            crossinline readList: IN.(
                    onField: IN.() -> Unit
            ) -> Unit
    ): AnySubReader<IN> = { typeInfo ->
        val valueSubtype = typeInfo.typeParameters.getOrNull(0)?.takeUnless { it.isStar }?.type
                ?: KxType(AnyReflection, true)
        val output = ArrayList<Any?>()
        val valueSubtypeReader = forReader.reader(valueSubtype.base.kclass)
        readList {
            output.add(valueSubtypeReader.invoke(this, valueSubtype))
        }
        output
    }
}