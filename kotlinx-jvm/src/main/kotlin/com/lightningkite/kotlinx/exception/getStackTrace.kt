package com.lightningkite.kotlinx.exception

import java.io.PrintWriter
import java.io.StringWriter

actual fun Exception.stackTraceString(): String {
    val underlying = StringWriter()
    val writer = PrintWriter(underlying)
    printStackTrace(writer)
    return underlying.toString()
}