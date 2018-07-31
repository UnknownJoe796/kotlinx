package com.lightningkite.kotlinx.reflection

data class KxFunction<Type>(
        val name: String,
        val type: KxType,
        val arguments: List<KxArgument> = listOf(),
        val call: (List<Any?>) -> Type,
        val annotations: List<KxAnnotation> = listOf()
)