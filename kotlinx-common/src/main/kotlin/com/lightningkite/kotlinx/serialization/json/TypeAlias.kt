package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.reflection.KxType

typealias JsonTypeReader<T> = RawJsonReader.(additionalTypeInformation: KxType?) -> T
typealias JsonTypeWriter<T> = RawJsonWriter.(additionalTypeInformation: KxType?, value: T) -> Unit