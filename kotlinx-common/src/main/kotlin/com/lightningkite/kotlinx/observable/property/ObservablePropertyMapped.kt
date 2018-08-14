


package com.lightningkite.kotlinx.observable.property


/**
 * Transforms an observable property from one type to another.
 * Created by jivie on 2/22/16.
 */
class ObservablePropertyMapped<S, T>(
        val observable: ObservableProperty<S>,
        val transformer: (S) -> T
) : EnablingMutableCollection<(T) -> Unit>(), ObservableProperty<T> {
    override val value: T
        get() = transformer(observable.value)

    val callback = { a: S ->
        val wrapped = transformer(a)
        forEach { it.invoke(wrapped) }
    }

    override fun enable() {
        observable.add(callback)
    }

    override fun disable() {
        observable.remove(callback)
    }
}

fun <S, T> ObservableProperty<S>.transform(mapper: (S) -> T): ObservablePropertyMapped<S, T> {
    return ObservablePropertyMapped(this, mapper)
}

@Deprecated("This is the same as using 'map'.", ReplaceWith("this.map(getterFun)", "com.lightningkite.kotlin.observable.property.map"))
fun <A, B> ObservableProperty<A>.sub(getterFun: (A) -> B) = ObservablePropertyMapped(this, getterFun)