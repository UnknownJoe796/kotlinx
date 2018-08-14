package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.observable.property.StandardObservableProperty
import com.lightningkite.kotlinx.observable.property.addAndInvoke

class RecoveringWebSocket(val url: String) : HttpWebSocket {

    val underlying = StandardObservableProperty<HttpWebSocket?>(null)
    var closed = false

    val connected = StandardObservableProperty(false)
    override var onBytesMessage: (ByteArray) -> Unit = {}
    override var onStringMessage: (String) -> Unit = {}
    override var onDisconnect: (closureCode: Int?, closureReason: String?, closureThrowable: Throwable?) -> Unit = { _, _, _ -> }

    override fun send(bytes: ByteArray) = underlying.value?.send(bytes) ?: Unit
    override fun send(text: String) = underlying.value?.send(text) ?: Unit

    init {
        underlying.addAndInvoke { under ->
            if (under == null) {
                connected.value = false
                HttpClient.socket(url).invoke {
                    it.onSuccess {
                        underlying.value = it
                    }
                }
            } else {
                connected.value = true
                under.onBytesMessage = { onBytesMessage.invoke(it) }
                under.onStringMessage = { onStringMessage.invoke(it) }
                under.onDisconnect = { code, reason, throwable ->
                    onDisconnect.invoke(code, reason, throwable)
                    if (!closed) {
                        underlying.value = null
                    }
                }
            }
        }
    }

    override fun close() {
        closed = true
        underlying.value?.close()
    }

    override fun close(code: Int, reason: String) {
        closed = true
        underlying.value?.close(code, reason)
    }

}