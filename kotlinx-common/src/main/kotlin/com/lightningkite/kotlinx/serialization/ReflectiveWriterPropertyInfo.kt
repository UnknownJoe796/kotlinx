package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.kxReflect
import com.lightningkite.kotlinx.reflection.untyped
import kotlin.reflect.KClass

data class ReflectiveWriterPropertyInfo<OUT, RESULT>(
        val key: String,
        val valueType: KxType,
        val getter: (Any) -> Any?,
        val writerObtainer: () -> AnySubWriter<OUT, RESULT>
) {
    val writer by lazy(writerObtainer)
    @Suppress("NOTHING_TO_INLINE")
    fun writeValue(out: OUT, on: Any): RESULT {
        return writer.invoke(out, getter(on), valueType)
    }
}

fun <OUT, RESULT> KClass<*>.reflectiveWriterData(
        anyWriter: AnyWriter<OUT, RESULT>
) = (kxReflect.values.values + kxReflect.variables.values).map {
    ReflectiveWriterPropertyInfo(
            key = it.name,
            valueType = it.type,
            getter = it.get.untyped,
            writerObtainer = { anyWriter.writer(it.type.base.kclass) }
    )
}