package com.lightningkite.kotlinx.reflection

import kotlin.reflect.*

expect val KAnnotatedElement.simpleAnnotations:List<SimpleAnnotation>
expect val <T:Any> KClass<T>.simpleProperties:Map<String,SimpleProperty1<T, *>>
expect val <T:Any> KClass<T>.simpleMutableProperties:Map<String,SimpleMutableProperty1<T, *>>