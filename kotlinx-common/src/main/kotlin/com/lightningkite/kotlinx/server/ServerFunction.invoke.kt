package com.lightningkite.kotlinx.server

import com.lightningkite.kotlinx.async.DelayedResultFunction
import com.lightningkite.kotlinx.httpclient.HttpBody
import com.lightningkite.kotlinx.httpclient.HttpClient
import com.lightningkite.kotlinx.httpclient.HttpMethod
import com.lightningkite.kotlinx.httpclient.HttpResponse
import com.lightningkite.kotlinx.serialization.ByteArraySerializer
import com.lightningkite.kotlinx.serialization.StringSerializer
import com.lightningkite.kotlinx.serialization.callSerializer
import com.lightningkite.kotlinx.serialization.json.JsonSerializer
import com.lightningkite.kotlinx.serialization.serializer

inline fun <reified T : Any> ServerFunction<T>.invoke(
        onEndpoint: String,
        headers: Map<String, List<String>> = mapOf(),
        serializer: StringSerializer = JsonSerializer
): DelayedResultFunction<HttpResponse<T>> = HttpClient.callSerializer<T>(
        url = onEndpoint,
        method = HttpMethod.POST,
        body = HttpBody.serializer(this, serializer),
        headers = headers
)

inline fun <reified T : Any> ServerFunction<T>.invoke(
        onEndpoint: String,
        headers: Map<String, List<String>> = mapOf(),
        serializer: ByteArraySerializer
): DelayedResultFunction<HttpResponse<T>> = HttpClient.callSerializer<T>(
        url = onEndpoint,
        method = HttpMethod.POST,
        body = HttpBody.serializer(this, serializer),
        headers = headers
)