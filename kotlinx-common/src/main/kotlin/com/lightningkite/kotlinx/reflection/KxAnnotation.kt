package com.lightningkite.kotlinx.reflection

data class KxAnnotation(
        val name: String,
        val arguments: List<Any> = listOf()
)