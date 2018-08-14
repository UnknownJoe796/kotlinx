package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.exception.stackTraceString
import com.lightningkite.kotlinx.httpclient.HttpWebSocket
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.kxType

inline fun <reified T : Any> HttpWebSocket.sendSerializer(
        value: T,
        serializer: ByteArraySerializer
) = sendSerializer(value, serializer, T::class.kxType)

fun <T> HttpWebSocket.sendSerializer(
        value: T,
        serializer: ByteArraySerializer,
        typeInfo: KxType
) = send(serializer.write(value, typeInfo))

inline fun <reified T : Any> HttpWebSocket.onSerializerMessage(
        serializer: ByteArraySerializer,
        crossinline action: (T) -> Unit
) = onSerializerMessage<T>(serializer, T::class.kxType, action)

inline fun <T> HttpWebSocket.onSerializerMessage(
        serializer: ByteArraySerializer,
        typeInfo: KxType,
        crossinline action: (T) -> Unit
) {
    onBytesMessage = {
        val value = serializer.read(it, typeInfo) as T
        action(value)
    }
}

class TypedByteArrayHttpWebSocket<CLIENT, SERVER>(
        val wraps: HttpWebSocket,
        val serverReader: ByteArraySerializer,
        val clientWriter: ByteArraySerializer = serverReader,
        val serverType: KxType,
        val clientType: KxType = serverType
) {
    var onMessage: (SERVER) -> Unit = {}
    var onDisconnect: (Int?, String?, Throwable?) -> Unit
        get() = wraps.onDisconnect
        set(value) {
            wraps.onDisconnect = value
        }

    fun close() = wraps.close()
    fun close(code: Int, reason: String) = wraps.close(code, reason)

    init {
        wraps.onBytesMessage = {
            try {
                @Suppress("UNCHECKED_CAST")
                onMessage.invoke(serverReader.read(it, serverType) as SERVER)
            } catch (e: Exception) {
                println(e.stackTraceString())
            }
        }
    }

    fun send(value: CLIENT) {
        wraps.send(clientWriter.write(value, clientType))
    }
}