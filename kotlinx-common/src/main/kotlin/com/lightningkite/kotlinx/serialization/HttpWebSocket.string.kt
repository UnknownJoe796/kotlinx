package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.exception.stackTraceString
import com.lightningkite.kotlinx.httpclient.HttpWebSocket
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.kxType
import com.lightningkite.kotlinx.serialization.json.JsonSerializer

inline fun <reified T : Any> HttpWebSocket.sendSerializer(
        value: T,
        serializer: StringSerializer = JsonSerializer
) = sendSerializer(value, serializer, T::class.kxType)

fun <T> HttpWebSocket.sendSerializer(
        value: T,
        serializer: StringSerializer = JsonSerializer,
        typeInfo: KxType
) = send(serializer.write(value, typeInfo))

inline fun <reified T : Any> HttpWebSocket.onSerializerMessage(
        serializer: StringSerializer = JsonSerializer,
        crossinline action: (T) -> Unit
) = onSerializerMessage<T>(serializer, T::class.kxType, action)

inline fun <T> HttpWebSocket.onSerializerMessage(
        serializer: StringSerializer = JsonSerializer,
        typeInfo: KxType,
        crossinline action: (T) -> Unit
) {
    onStringMessage = {
        val value = serializer.read(it, typeInfo) as T
        action(value)
    }
}

class TypedStringHttpWebSocket<CLIENT, SERVER>(
        val wraps: HttpWebSocket,
        val serverReader: StringSerializer,
        val clientWriter: StringSerializer = serverReader,
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
        wraps.onStringMessage = {
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