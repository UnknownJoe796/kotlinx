package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.observable.property.ObservableProperty
import com.lightningkite.kotlinx.utils.Closeable

expect class HttpWebSocket(url: String) : Closeable {
    val connected: ObservableProperty<Boolean>
    var onBytesMessage: (ByteArray) -> Unit
    var onStringMessage: (String) -> Unit
    fun send(bytes: ByteArray)
    fun send(text: String)
    val closureCode: Int?
    val closureReason: String?
    val closureThrowable: Throwable?
    override fun close()
    fun close(code: Int, reason: String)
}