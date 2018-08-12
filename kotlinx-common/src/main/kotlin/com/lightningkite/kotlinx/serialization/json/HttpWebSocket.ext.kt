package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.httpclient.HttpWebSocket
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.kxType

inline fun <reified T : Any> HttpWebSocket.sendJson(
        value: T,
        serializer: JsonSerializer = JsonSerializer
) = sendJson(value, serializer, T::class.kxType)

fun <T> HttpWebSocket.sendJson(
        value: T,
        serializer: JsonSerializer = JsonSerializer,
        typeInfo: KxType
) = send(serializer.write(typeInfo, value))

inline fun <reified T : Any> HttpWebSocket.onJsonMessage(
        serializer: JsonSerializer = JsonSerializer,
        crossinline action: (T) -> Unit
) = onJsonMessage<T>(serializer, T::class.kxType, action)

inline fun <T> HttpWebSocket.onJsonMessage(
        serializer: JsonSerializer = JsonSerializer,
        typeInfo: KxType,
        crossinline action: (T) -> Unit
) {
    onStringMessage = {
        val value = serializer.read(typeInfo, it) as T
        action(value)
    }
}