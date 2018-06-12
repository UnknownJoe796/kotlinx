package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KType

class SimpleProperty1<R, T>(
        val name:String,
        val type: KType,
        val getter:(R)->T,
        val simpleAnnotations:List<SimpleAnnotation>
)