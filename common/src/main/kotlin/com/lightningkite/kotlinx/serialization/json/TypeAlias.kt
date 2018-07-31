package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.KxType

typealias JsonTypeReader<T> = JsonReader.(additionalTypeInformation:KxType?) -> T
typealias JsonTypeWriter<T> = JsonWriter.(additionalTypeInformation:KxType?, value: T) -> Unit