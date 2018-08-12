package com.lightningkite.kotlinx.server

import com.lightningkite.kotlinx.reflection.*

@ExternalReflection
sealed class ModificationOnItem<in T : Any> {

    abstract operator fun invoke(item: T)

    @ExternalReflection
    data class Set<T : Any, V>(val field: KxVariable<T, V>, val value: V) : ModificationOnItem<T>() {
        override fun invoke(item: T) {
            field.set(item, value)
        }
    }

    @ExternalReflection
    data class Add<T : Any, V : Number, I>(val field: KxVariable<T, V>, val amount: V) : ModificationOnItem<T>() {
        override fun invoke(item: T) {
            @Suppress("IMPLICIT_CAST_TO_ANY")
            val newValue = when (field.type.base) {
                ByteReflection -> field.get(item).let { it as Byte? }?.plus(amount as Byte)
                ShortReflection -> field.get(item).let { it as Short? }?.plus(amount as Short)
                IntReflection -> field.get(item).let { it as Int? }?.plus(amount as Int)
                LongReflection -> field.get(item).let { it as Long? }?.plus(amount as Long)
                FloatReflection -> field.get(item).let { it as Float? }?.plus(amount as Float)
                DoubleReflection -> field.get(item).let { it as Double? }?.plus(amount as Double)
                else -> throw IllegalArgumentException()
            }
            field.set.untyped(item, newValue)
        }
    }

    @ExternalReflection
    data class Multiply<T : Any, V : Number, I>(val field: KxVariable<T, V>, val amount: V) : ModificationOnItem<T>() {
        override fun invoke(item: T) {
            @Suppress("IMPLICIT_CAST_TO_ANY")
            val newValue = when (field.type.base) {
                ByteReflection -> field.get(item).let { it as Byte? }?.times(amount as Byte)
                ShortReflection -> field.get(item).let { it as Short? }?.times(amount as Short)
                IntReflection -> field.get(item).let { it as Int? }?.times(amount as Int)
                LongReflection -> field.get(item).let { it as Long? }?.times(amount as Long)
                FloatReflection -> field.get(item).let { it as Float? }?.times(amount as Float)
                DoubleReflection -> field.get(item).let { it as Double? }?.times(amount as Double)
                else -> throw IllegalArgumentException()
            }
            field.set.untyped(item, newValue)
        }
    }

    @ExternalReflection
    data class Place<T : Any, V : MutableCollection<I>, I>(val field: KxField<T, V>, val element: V) : ModificationOnItem<T>() {
        override fun invoke(item: T) {
            @Suppress("UNCHECKED_CAST")
            field.get(item).add(element as I)
        }
    }

    @ExternalReflection
    data class Remove<T : Any, V : MutableCollection<I>, I>(val field: KxField<T, V>, val element: V) : ModificationOnItem<T>() {
        override fun invoke(item: T) {
            @Suppress("UNCHECKED_CAST")
            field.get(item).remove(element as I)
        }
    }
}

fun <T : Any> Iterable<ModificationOnItem<T>>.invoke(item: T) = forEach { it.invoke(item) }