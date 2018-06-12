package com.lightningkite.kotlinx.reflection

import com.lightningkite.kotlinx.collection.WeakHashMap
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1


private val KClassSimpleProperties = HashMap<KClass<*>, Map<String, SimpleProperty1<*, *>>>()
@Suppress("UNCHECKED_CAST")
actual val <T:Any> KClass<T>.simpleProperties:Map<String,SimpleProperty1<T, *>>
    get() = mapOf() //TODO

private val KClassSimpleMutableProperties = HashMap<KClass<*>, Map<String, SimpleMutableProperty1<*, *>>>()
@Suppress("UNCHECKED_CAST")
actual val <T:Any> KClass<T>.simpleMutableProperties:Map<String,SimpleMutableProperty1<T, *>>
    get() = mapOf() //TODO

private val KAnnotatedElementSimpleAnnotations = HashMap<KAnnotatedElement, List<SimpleAnnotation>>()
@Suppress("UNCHECKED_CAST")
actual val KAnnotatedElement.simpleAnnotations:List<SimpleAnnotation>
    get() = listOf() //TODO