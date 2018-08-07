package com.lightningkite.kotlinx.reflection

@Suppress("UNCHECKED_CAST")
val <Owner, Type> ((Owner) -> Type).untyped
    get() = this as ((Any) -> Any?)

@Suppress("UNCHECKED_CAST")
val <Owner, Type> ((Owner, Type) -> Unit).untyped
    get() = this as ((Any, Any?) -> Unit)