package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KClass

abstract class EmptyReflection<T: Any>(override val kclass: KClass<T>, override val qualifiedName: String) : KxClass<T> {
    override val simpleName: String by lazy { qualifiedName.substringAfterLast('.') }
    override val implements: List<KxType> = listOf()
    override val isInterface: Boolean = false
    override val isOpen: Boolean = false
    override val isAbstract: Boolean = false
    override val enumValues: List<T>? = null
    override val values: Map<String, KxValue<T, *>> = mapOf()
    override val variables: Map<String, KxVariable<T, *>> = mapOf()
    override val functions: List<KxFunction<*>> = listOf()
    override val constructors: List<KxFunction<T>> = listOf()
    override val annotations: List<KxAnnotation> = listOf()
}