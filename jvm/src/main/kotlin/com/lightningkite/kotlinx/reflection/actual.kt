package com.lightningkite.kotlinx.reflection

import com.lightningkite.kotlinx.collection.WeakHashMap
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties


private val KClassSimpleProperties = HashMap<KClass<*>, Map<String, SimpleProperty1<*, *>>>()
@Suppress("UNCHECKED_CAST")
actual val <T:Any> KClass<T>.simpleProperties:Map<String,SimpleProperty1<T, *>>
    get() = KClassSimpleProperties.getOrPut(this){
        this.memberProperties.mapNotNull { it as? KMutableProperty1<T, *> }
                .associate {
                    it.name to SimpleProperty1<T, Any?>(
                            name = it.name,
                            type = it.returnType,
                            getter = it.getter,
                            simpleAnnotations = it.simpleAnnotations
                    )
                }
    } as Map<String, SimpleProperty1<T, *>>

private val KClassSimpleMutableProperties = HashMap<KClass<*>, Map<String, SimpleMutableProperty1<*, *>>>()
@Suppress("UNCHECKED_CAST")
actual val <T:Any> KClass<T>.simpleMutableProperties:Map<String,SimpleMutableProperty1<T, *>>
    get() = KClassSimpleMutableProperties.getOrPut(this){
        this.memberProperties.mapNotNull { it as? KMutableProperty1<T, *> }
                .associate {
                    it.name to SimpleMutableProperty1<T, Any?>(
                            name = it.name,
                            type = it.returnType,
                            getter = it.getter,
                            setter = it.setter as ((T, Any?)->Unit),
                            simpleAnnotations = it.simpleAnnotations
                    )
                }
    } as Map<String, SimpleMutableProperty1<T, *>>

private val KAnnotatedElementSimpleAnnotations = HashMap<KAnnotatedElement, List<SimpleAnnotation>>()
@Suppress("UNCHECKED_CAST")
actual val KAnnotatedElement.simpleAnnotations:List<SimpleAnnotation>
    get() = KAnnotatedElementSimpleAnnotations.getOrPut(this){
        annotations.map { ann ->
            SimpleAnnotation(
                    name = ann.annotationClass.qualifiedName!!,
                    values = ann.annotationClass.memberProperties.associate {
                        it.name to (it as KProperty1<Any, Any?>).get(ann)
                    }
            )
        }
    }