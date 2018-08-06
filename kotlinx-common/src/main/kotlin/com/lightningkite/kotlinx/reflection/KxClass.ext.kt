package com.lightningkite.kotlinx.reflection

import com.lightningkite.kotlinx.collection.WeakHashMap

private val KxClass_allImplements = WeakHashMap<KxClass<*>, List<KxType>>()
val KxClass<*>.allImplements: List<KxType>
    get() = KxClass_allImplements.getOrPut(this) {
        implements.flatMap { it.base.allImplements }.distinctBy { it.base }
    }