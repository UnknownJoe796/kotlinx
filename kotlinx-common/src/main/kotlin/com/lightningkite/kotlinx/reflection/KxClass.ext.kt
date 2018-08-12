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

private val KxClass_kxType = HashMap<KxClass<*>, KxType>()
val <T : Any> KxClass<T>.kxType
    get() = KxClass_kxType.getOrPut(this) { KxType(this) }

private val KxClass_kxTypeNullable = HashMap<KxClass<*>, KxType>()
val <T : Any> KxClass<T>.kxTypeNullable
    get() = KxClass_kxTypeNullable.getOrPut(this) { KxType(this, true) }

val KxClass<*>.canBeExtended: Boolean
    get() = modifiers.contains(KxClassModifier.Open) ||
            modifiers.contains(KxClassModifier.Interface) ||
            modifiers.contains(KxClassModifier.Abstract) ||
            modifiers.contains(KxClassModifier.Sealed)

val KxClass<*>.canBeInstantiated: Boolean
    get() = !(modifiers.contains(KxClassModifier.Interface) ||
            modifiers.contains(KxClassModifier.Abstract) ||
            modifiers.contains(KxClassModifier.Sealed))