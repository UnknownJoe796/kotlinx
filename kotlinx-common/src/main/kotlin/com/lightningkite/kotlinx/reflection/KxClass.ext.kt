package com.lightningkite.kotlinx.reflection

import com.lightningkite.kotlinx.collection.WeakHashMap

private val KxClass_allImplements = WeakHashMap<KxClass<*>, List<KxType>>()
val KxClass<*>.allImplements: List<KxType>
    get() = KxClass_allImplements.getOrPut(this) {
        implements.flatMap { it.base.allImplements }.distinctBy { it.base }
    }

inline fun Collection<KxVariable<*, *>>.forEachOnInstance(
        instance: Any,
        action: (KxVariable<*, *>, Any?) -> Unit
) = forEach {
    val value = it.get.untyped.invoke(instance)
    action(it, value)
}