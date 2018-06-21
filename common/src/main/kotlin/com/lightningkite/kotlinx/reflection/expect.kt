package com.lightningkite.kotlinx.reflection

import kotlin.reflect.KClass

internal expect fun <T : Any> kxReflect(underlying: KClass<T>): KxClass<T>