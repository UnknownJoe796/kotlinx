package com.lightningkite.kotlinx.serialization

import com.lightningkite.kotlinx.async.DelayedResultFunction
import com.lightningkite.kotlinx.async.map
import com.lightningkite.kotlinx.httpclient.*
import com.lightningkite.kotlinx.reflection.KxType
import com.lightningkite.kotlinx.reflection.kxType
import com.lightningkite.kotlinx.serialization.json.JsonSerializer

inline fun <reified T : Any> HttpClient.callSerializer(
        url: String,
        method: HttpMethod,
        body: HttpBody,
        headers: Map<String, List<String>> = mapOf(),
        serializer: StringSerializer = JsonSerializer
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
        serializer: StringSerializer = JsonSerializer,
        typeInfo: KxType
): DelayedResultFunction<HttpResponse<T>> {
    return callString(
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
        serializer: StringSerializer = JsonSerializer
) = serializer<T>(
        value = value,
        serializer = serializer,
        typeInfo = T::class.kxType
)


@Suppress("UNCHECKED_CAST")
fun <T : Any> HttpBody.Companion.serializer(
        value: T,
        serializer: StringSerializer = JsonSerializer,
        typeInfo: KxType
) = HttpBody.BString(serializer.contentType, serializer.write(value, typeInfo))