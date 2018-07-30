package com.lightningkite.kotlinx.serialization.json

typealias JsonTypeReader<T> = JsonReader.() -> T
typealias JsonTypeWriter<T> = JsonWriter.(T) -> Unit