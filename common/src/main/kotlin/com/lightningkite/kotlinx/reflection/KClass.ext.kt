package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KClass

val KClass_kxReflect = HashMap<KClass<*>, KxClass<*>>()
val <T : Any> KClass<T>.kxReflect
    get() = KClass_kxReflect.getOrPut(this) {
        kxReflect(this)
    } as KxClass<T>