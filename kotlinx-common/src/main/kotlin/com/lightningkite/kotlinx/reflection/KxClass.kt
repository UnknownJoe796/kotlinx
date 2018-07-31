package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KClass

interface KxClass<Owner: Any> {
    val kclass: KClass<Owner>

    val simpleName: String
    val qualifiedName: String

    val isInterface: Boolean
    val isOpen: Boolean
    val isAbstract: Boolean
    val enumValues: List<Owner>?

    val values: Map<String, KxValue<Owner, *>>
    val variables: Map<String, KxVariable<Owner, *>>
    val functions: List<KxFunction<*>>
    val constructors: List<KxFunction<Owner>>
    val annotations: List<KxAnnotation>
}