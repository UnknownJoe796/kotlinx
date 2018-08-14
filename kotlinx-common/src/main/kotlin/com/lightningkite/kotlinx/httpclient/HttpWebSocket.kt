package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.utils.Closeable

interface HttpWebSocket : Closeable {
    var onBytesMessage: (ByteArray) -> Unit
    var onStringMessage: (String) -> Unit
    var onDisconnect: (
            closureCode: Int?,
            closureReason: String?,
            closureThrowable: Throwable?
    ) -> Unit

    fun send(bytes: ByteArray)
    fun send(text: String)
    override fun close()
    fun close(code: Int, reason: String)
}