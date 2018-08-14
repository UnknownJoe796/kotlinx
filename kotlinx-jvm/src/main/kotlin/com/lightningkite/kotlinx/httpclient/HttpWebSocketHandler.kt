package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.lambda.once
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.io.Closeable

class HttpWebSocketHandler internal constructor() : HttpWebSocket, WebSocketListener(), Closeable {
    lateinit var underlying: WebSocket
    lateinit var onComplete: (SuccessOrFailure<HttpWebSocketHandler>) -> Unit
    private var privateClosureThrowable: Throwable? = null

    fun init(underlying: WebSocket, complete: (SuccessOrFailure<HttpWebSocketHandler>) -> Unit) {
        this.underlying = underlying
        this.onComplete = once(complete)
    }

    override var onBytesMessage: (ByteArray) -> Unit = {}
    override var onStringMessage: (String) -> Unit = {}
    override var onDisconnect: (closureCode: Int?, closureReason: String?, closureThrowable: Throwable?) -> Unit = { _, _, _ -> }

    override fun send(bytes: ByteArray) {
        underlying.send(ByteString.of(bytes, 0, bytes.size))
    }

    override fun send(text: String) {
        underlying.send(text)
    }

    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        onComplete.invoke(SuccessOrFailure.success(this))
    }

    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        privateClosureThrowable = t
        onComplete.invoke(SuccessOrFailure.failure(t ?: Exception()))
    }

    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {}

    override fun onMessage(webSocket: WebSocket?, text: String) {
        HttpClient.resultThread.invoke {
            onStringMessage.invoke(text)
        }
    }

    override fun onMessage(webSocket: WebSocket?, bytes: ByteString) {
        HttpClient.resultThread.invoke {
            onBytesMessage.invoke(bytes.toByteArray())
        }
    }

    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        onDisconnect.invoke(code, reason, privateClosureThrowable)
    }

    override fun close() = close(1000, "Normal closure")
    override fun close(code: Int, reason: String) {
        underlying.close(code, reason)
    }
}