package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.reflection.canBeInstantiated
import com.lightningkite.kotlinx.reflection.kxReflect
import kotlin.reflect.KClass

private val KClassSerializePolymorphic = HashMap<KClass<*>, Boolean>()
var KClass<*>.serializePolymorphic: Boolean
    get() = KClassSerializePolymorphic.getOrPut(this) {
        !this.kxReflect.canBeInstantiated
    }
    set(value) {
        KClassSerializePolymorphic[this] = value
    }