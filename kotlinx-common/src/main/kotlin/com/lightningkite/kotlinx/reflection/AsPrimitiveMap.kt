package com.lightningkite.kotlinx.reflection

//fun Any.asPrimitive():Any? = when (this) {
//    is Unit,
//    is Boolean,
//    is Byte,
//    is Short,
//    is Int,
//    is Long,
//    is Float,
//    is Double,
//    is Char,
//    is String -> this
//    is List<*> -> (this as List<Any?>).map{ it?.asPrimitive() }
//    is Map<*, *> -> (this as Map<Any?, Any?>).entries.associate { it.key?.asPrimitive() to it.value?.asPrimitive() }
//}