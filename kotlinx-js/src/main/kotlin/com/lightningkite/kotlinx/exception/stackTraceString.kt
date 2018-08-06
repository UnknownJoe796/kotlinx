package com.lightningkite.kotlinx.exception

actual fun Exception.stackTraceString(): String {
    return generateSequence<Throwable>(this) { it.cause }.joinToString("\n") {
        it.toString() + ": " + it.message
    }
}