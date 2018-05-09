package com.lightningkite.kotlinx.async

typealias DelayedResultFunction<T> = ((T)->Unit)->Unit

fun <A, B> DelayedResultFunction<A>.then(makeNext:(A)->DelayedResultFunction<B>):DelayedResultFunction<B>{
    return { callbackB ->
        this.invoke {
            makeNext(it).invoke(callbackB)
        }
    }
}

inline fun <A, B> DelayedResultFunction<A>.transform(crossinline transform:(A)->B):DelayedResultFunction<B> {
    return { callbackB ->
        this.invoke {
            callbackB.invoke(transform.invoke(it) )
        }
    }
}

fun <A> DelayedResultFunction<A>.prepend(first:DelayedResultFunction<*>):DelayedResultFunction<A>{
    return { callback ->
        first.invoke {
            this.invoke(callback)
        }
    }
}