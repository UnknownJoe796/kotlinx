package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.async.DelayedResultFunction
import com.lightningkite.kotlinx.async.map
import com.lightningkite.kotlinx.httpclient.*
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.kxType

inline fun <reified T : Any> HttpClient.callSerializer(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>> = mapOf(),
        serializer: ByteArraySerializer
): DelayedResultFunction<HttpResponse<T>> = callSerializer(
        url,
        method,
        body,
        headers,
        serializer,
        T::class.kxType
)


fun <T : Any> HttpClient.callSerializer(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>> = mapOf(),
        serializer: ByteArraySerializer,
        typeInfo: KxType
): DelayedResultFunction<HttpResponse<T>> {
    return callByteArray(
            url, method, body, headers
    ).map {
        it.copy {
            @Suppress("UNCHECKED_CAST")
            serializer.read(it, typeInfo) as T
        }
    }
}

inline fun <reified T : Any> HttpBody.Companion.serializer(
        value: T,
        serializer: ByteArraySerializer
) = serializer<T>(
        value = value,
        serializer = serializer,
        typeInfo = T::class.kxType
)


@Suppress("UNCHECKED_CAST")
fun <T : Any> HttpBody.Companion.serializer(
        value: T,
        serializer: ByteArraySerializer,
        typeInfo: KxType
) = HttpBody.BByteArray(serializer.contentType, serializer.write(value, typeInfo))