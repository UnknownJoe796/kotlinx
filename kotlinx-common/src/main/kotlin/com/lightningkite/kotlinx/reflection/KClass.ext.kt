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
