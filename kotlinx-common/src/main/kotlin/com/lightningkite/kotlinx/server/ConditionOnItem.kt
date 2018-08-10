package com.lightningkite.kotlinx.server

import com.lightningkite.kotlinx.reflection.KxField

sealed class ConditionOnItem<in T> {

    abstract operator fun invoke(item: T): Boolean

    interface OnField<T, V> {
        val field: KxField<T, V>
    }

    class Never<T> : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = false
    }

    class Always<T> : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = true
    }

    class And<T>(vararg val conditions: ConditionOnItem<T>) : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = conditions.all { it(item) }
    }

    class Or<T>(vararg val conditions: ConditionOnItem<T>) : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = conditions.any { it(item) }
    }

    class Not<T>(val condition: ConditionOnItem<T>) : ConditionOnItem<T>() {
        override fun invoke(item: T): Boolean = !condition(item)
    }

    class Equal<T, V>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) == value
    }

    class EqualToOne<T, V>(override val field: KxField<T, V>, val values: Collection<V>) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) in values
    }

    class NotEqual<T, V>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) != value
    }

    class LessThan<T, V : Comparable<V>>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) < value
    }

    class GreaterThan<T, V : Comparable<V>>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) > value
    }

    class LessThanOrEqual<T, V : Comparable<V>>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) <= value
    }

    class GreaterThanOrEqual<T, V : Comparable<V>>(override val field: KxField<T, V>, val value: V) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item) >= value
    }

    class TextSearch<T, V : CharSequence>(override val field: KxField<T, V>, val query: String) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item).contains(query)
    }

    class RegexTextSearch<T, V : CharSequence>(override val field: KxField<T, V>, val query: String) : ConditionOnItem<T>(), OnField<T, V> {
        override fun invoke(item: T): Boolean = field.get.invoke(item).contains(Regex(query))
    }
}