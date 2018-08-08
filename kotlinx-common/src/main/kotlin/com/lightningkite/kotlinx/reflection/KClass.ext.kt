package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
var <T : Any> KClass<T>.kxReflect
    get() = KxReflection.map[this] as KxClass<T>
    set(value) {
        KxReflection.map[this] = value
    }

@Suppress("UNCHECKED_CAST")
val <T : Any> KClass<T>.kxReflectOrNull
    get() = KxReflection.map[this] as? KxClass<T>

private val KClass_kxType = HashMap<KClass<*>, KxType>()
val <T : Any> KClass<T>.kxType
    get() = KClass_kxType.getOrPut(this) { KxType(kxReflect) }

private val KClass_kxTypeNullable = HashMap<KClass<*>, KxType>()
val <T : Any> KClass<T>.kxTypeNullable
    get() = KClass_kxTypeNullable.getOrPut(this) { KxType(kxReflect, true) }