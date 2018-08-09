package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.kxReflectOrNull
import com.lightningkite.kotlinx.reflection.untyped
import kotlin.reflect.KClass

data class ReflectiveWriterPropertyInfo<OUT, RESULT>(
        val key: String,
        val valueType: KxType,
        val getter: (Any) -> Any?,
        val writer: AnySubWriter<OUT, RESULT>
) {
    @Suppress("NOTHING_TO_INLINE")
    inline fun writeValue(out: OUT, on: Any): RESULT {
        return writer.invoke(out, getter(on), valueType)
    }
}

fun <OUT, RESULT> KClass<*>.reflectiveWriterData(
        anyWriter: AnyWriter<OUT, RESULT>
) = kxReflectOrNull?.variables?.values?.map {
    ReflectiveWriterPropertyInfo(
            key = it.name,
            valueType = it.type,
            getter = it.get.untyped,
            writer = anyWriter.writer(this)
    )
}