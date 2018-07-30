package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KClass

val KClass_kxReflect = HashMap<KClass<*>, KxClass<*>>()
var <T : Any> KClass<T>.kxReflect
    get() = KClass_kxReflect[this] as KxClass<T>
    set(value) {
        KClass_kxReflect[this] = value
    }

val <T : Any> KClass<T>.kxReflectOrNull
    get() = KClass_kxReflect[this] as? KxClass<T>
