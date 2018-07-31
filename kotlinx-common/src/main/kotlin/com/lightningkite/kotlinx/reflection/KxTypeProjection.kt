package com.lightningkite.kotlinx.reflection

data class KxTypeProjection(
        val type: KxType,
        val variance: KxVariance = KxVariance.INVARIANT,
        val isStar: Boolean = false
) {
    companion object {
        val STAR = KxTypeProjection(KxType(Any::class.kxReflect, true), isStar = true)
    }
}