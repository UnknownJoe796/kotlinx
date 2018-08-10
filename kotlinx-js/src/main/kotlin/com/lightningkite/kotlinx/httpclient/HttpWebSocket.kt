package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.asByteArray
import com.lightningkite.kotlinx.asInt8Array
import com.lightningkite.kotlinx.observable.property.ObservableProperty
import com.lightningkite.kotlinx.observable.property.StandardObservableProperty
import com.lightningkite.kotlinx.utils.Closeable
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.CloseEvent
import org.w3c.dom.WebSocket

actual class HttpWebSocket actual constructor(url: String) : Closeable {
    val underlying = WebSocket(url)

    val privateConnected = StandardObservableProperty(false)
    actual val connected: ObservableProperty<Boolean> get() = privateConnected
    actual var onBytesMessage: (ByteArray) -> Unit = {}
    actual var onStringMessage: (String) -> Unit = {}

    actual fun send(bytes: ByteArray) {
        underlying.send(bytes.asInt8Array().buffer)
    }

    actual fun send(text: String) {
        underlying.send(text)
    }

    actual val closureCode: Int? get() = privateClosureCode
    var privateClosureCode: Int? = null
    actual val closureReason: String? get() = privateClosureReason
    var privateClosureReason: String? = null
    actual val closureThrowable: Throwable? get() = privateClosureThrowable
    var privateClosureThrowable: Throwable? = null

    init {
        underlying.binaryType = BinaryType.ARRAYBUFFER
        underlying.onopen = {
            privateConnected.value = true
            Unit
        }
        underlying.onmessage = {
            @Suppress("USELESS_CAST")
            val received = it.asDynamic().data as Any?
            when (received) {
                is String -> onStringMessage.invoke(received)
                is ArrayBuffer -> onBytesMessage.invoke(Int8Array(received).asByteArray())
            }
            Unit
        }
        underlying.onclose = {
            privateConnected.value = false
            val event = it as CloseEvent
            privateClosureCode = event.code.toInt()
            privateClosureReason = event.reason
            Unit
        }
    }

    actual override fun close() = close(1000, "Normal closure")
    actual fun close(code: Int, reason: String) {
        underlying.close(code.toShort(), reason)
    }
}