package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KClass

interface KxClass<Owner: Any> {
    val kclass: KClass<Owner>

    val implements: List<KxType>

    val simpleName: String
    val qualifiedName: String

    val modifiers: List<KxClassModifier>
    val enumValues: List<Owner>?

    val values: Map<String, KxValue<Owner, *>>
    val variables: Map<String, KxVariable<Owner, *>>
    val functions: List<KxFunction<*>>
    val constructors: List<KxFunction<Owner>>
    val annotations: List<KxAnnotation>
}