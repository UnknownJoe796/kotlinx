package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.kxReflectOrNull
import com.lightningkite.kotlinx.reflection.kxType

object EnumGenerators {

    fun <IN> readerGenerator(forReader: StandardReader<IN>): AnySubReaderGenerator<IN> = generator@{ type ->
        val mapped = type.kxReflectOrNull?.enumValues?.associate { (it as Enum<*>).name.toLowerCase() to it }
                ?: return@generator null

        //Cache the reader
        val stringReader = forReader.reader(String::class)
        val stringKxType = String::class.kxType

        return@generator { _ ->
            val name = stringReader.invoke(this, stringKxType).let { it as String }.toLowerCase()
            mapped[name] ?: throw SerializationException("Enum value $name not recognized.")
        }
    }

    fun <OUT, RESULT> writerGenerator(
            forWriter: StandardWriter<OUT, RESULT>
    ): AnySubWriterGenerator<OUT, RESULT> = generator@{ type ->
        if (type.kxReflectOrNull?.enumValues == null) return@generator null

        //Cache the writer
        val stringWriter = forWriter.writer(String::class)
        val stringKxType = String::class.kxType

        return@generator { value, _ ->
            stringWriter.invoke(this, (value as Enum<*>).name, stringKxType)
        }
    }
}