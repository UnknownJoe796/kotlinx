package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KType

class SimpleMutableProperty1<R, T>(
        val name:String,
        val type: KType,
        val getter:(R)->T,
        val setter:(R, T)->Unit,
        val simpleAnnotations:List<SimpleAnnotation>
)