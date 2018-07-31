package com.lightningkite.kotlinx.reflection

data class KxType(
        val base: KxClass<*>,
        val nullable: Boolean = false,
        val typeParameters: List<KxTypeProjection> = listOf(),
        val annotations: List<KxAnnotation> = listOf()
)