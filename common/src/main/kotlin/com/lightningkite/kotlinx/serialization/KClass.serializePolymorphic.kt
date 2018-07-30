package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.kxReflectOrNull
import kotlin.reflect.KClass

private val KClassSerializePolymorphic = HashMap<KClass<*>, Boolean>()
var KClass<*>.serializePolymorphic: Boolean
    get() = KClassSerializePolymorphic.getOrPut(this) {
        this.kxReflectOrNull?.run { isAbstract || isInterface } ?: false
    }
    set(value) {
        KClassSerializePolymorphic[this] = value
    }