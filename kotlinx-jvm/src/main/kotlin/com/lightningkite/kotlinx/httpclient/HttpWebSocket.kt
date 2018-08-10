package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.observable.property.ObservableProperty
import com.lightningkite.kotlinx.observable.property.StandardObservableProperty
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.Closeable

actual class HttpWebSocket actual constructor(url: String) : WebSocketListener(), Closeable {

    val underlying = HttpClient.okClient.newWebSocket(
            Request.Builder().url(url).build(),
            this
    )

    val privateConnected = StandardObservableProperty(false)
    actual val connected: ObservableProperty<Boolean> get() = privateConnected
    actual var onBytesMessage: (ByteArray) -> Unit = {}
    actual var onStringMessage: (String) -> Unit = {}

    actual fun send(bytes: ByteArray) {
        underlying.send(ByteString.of(bytes, 0, bytes.size))
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


    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        privateConnected.value = true
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        privateClosureThrowable = t
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        privateConnected.value = false
        privateClosureCode = code
        privateClosureReason = reason
    }

    override fun onMessage(webSocket: WebSocket?, text: String) {
        onStringMessage.invoke(text)
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
        onBytesMessage.invoke(bytes.toByteArray())
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        privateConnected.value = false
        privateClosureCode = code
        privateClosureReason = reason
    }

    actual override fun close() = close(1000, "Normal closure")
    actual fun close(code: Int, reason: String) {
        underlying.close(code, reason)
    }
}