package com.lightningkite.kotlinx.reflection

data class KxArgument(
        val name: String,
        val type: KxType,
        val annotations: List<KxAnnotation> = listOf(),
        val default: ((previousArgument:List<Any?>)->Any?)? = null
)