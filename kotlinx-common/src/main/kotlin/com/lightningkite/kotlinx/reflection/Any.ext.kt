package com.lightningkite.kotlinx.reflection


fun <T : Any> T.setMap(given: Map<KxVariable<T, *>, Any?>) {
    for ((v, value) in given) {
        v.set.untyped.invoke(this, value)
    }
}

fun Any.setUntyped(given: Map<KxVariable<*, *>, Any?>) {
    for ((v, value) in given) {
        v.set.untyped.invoke(this, value)
    }
}

fun Any.setUntyped(given: List<Pair<KxVariable<*, *>, Any?>>) {
    for ((v, value) in given) {
        v.set.untyped.invoke(this, value)
    }
}