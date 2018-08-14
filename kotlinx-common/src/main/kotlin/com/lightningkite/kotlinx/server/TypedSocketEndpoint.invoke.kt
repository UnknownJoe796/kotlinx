package com.lightningkite.kotlinx.server

import com.lightningkite.kotlinx.async.map
import com.lightningkite.kotlinx.httpclient.HttpClient
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.serialization.ByteArraySerializer
import com.lightningkite.kotlinx.serialization.StringSerializer
import com.lightningkite.kotlinx.serialization.TypedByteArrayHttpWebSocket
import com.lightningkite.kotlinx.serialization.TypedStringHttpWebSocket
import com.lightningkite.kotlinx.serialization.json.JsonSerializer

fun <CLIENT, SERVER> TypedSocketEndpoint<CLIENT, SERVER>.invoke(
        onEndpoint: String,
        serverReader: StringSerializer = JsonSerializer,
        clientWriter: StringSerializer = serverReader,
        serverType: KxType,
        clientType: KxType = serverType
) = HttpClient.socket(onEndpoint).map {
    it.map {
        TypedStringHttpWebSocket<CLIENT, SERVER>(
                wraps = it,
                serverReader = serverReader,
                clientWriter = clientWriter,
                serverType = serverType,
                clientType = clientType
        )
    }
}

fun <CLIENT, SERVER> TypedSocketEndpoint<CLIENT, SERVER>.invoke(
        onEndpoint: String,
        serverReader: ByteArraySerializer,
        clientWriter: ByteArraySerializer = serverReader,
        serverType: KxType,
        clientType: KxType = serverType
) = HttpClient.socket(onEndpoint).map {
    it.map {
        TypedByteArrayHttpWebSocket<CLIENT, SERVER>(
                wraps = it,
                serverReader = serverReader,
                clientWriter = clientWriter,
                serverType = serverType,
                clientType = clientType
        )
    }
}