package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.asByteArray
import com.lightningkite.kotlinx.asInt8Array
import com.lightningkite.kotlinx.lambda.once
import com.lightningkite.kotlinx.utils.Closeable
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.dom.ARRAYBUFFER
import org.w3c.dom.BinaryType
import org.w3c.dom.CloseEvent
import org.w3c.dom.WebSocket

class HttpWebSocketImpl(url: String, onReady: (SuccessOrFailure<HttpWebSocketImpl>) -> Unit) : HttpWebSocket, Closeable {
    val onReady = once(onReady)
    val underlying = WebSocket(url)

    override var onBytesMessage: (ByteArray) -> Unit = {}
    override var onStringMessage: (String) -> Unit = {}
    override var onDisconnect: (closureCode: Int?, closureReason: String?, closureThrowable: Throwable?) -> Unit = { _, _, _ -> }

    override fun send(bytes: ByteArray) {
        underlying.send(bytes.asInt8Array().buffer)
    }

    override fun send(text: String) {
        underlying.send(text)
    }

    init {
        underlying.binaryType = BinaryType.ARRAYBUFFER
        underlying.onopen = {
            onReady.invoke(SuccessOrFailure.success(this))
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
        underlying.onerror = {
            onReady.invoke(SuccessOrFailure.failure(Exception()))
        }
        underlying.onclose = {
            val event = it as CloseEvent

            onDisconnect.invoke(
                    event.code.toInt(),
                    event.reason,
                    null
            )
            Unit
        }
    }

    override fun close() = close(1000, "Normal closure")
    override fun close(code: Int, reason: String) {
        underlying.close(code.toShort(), reason)
    }
}