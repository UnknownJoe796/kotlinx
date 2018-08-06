package com.lightningkite.kotlinx.server

import com.lightningkite.kotlinx.async.DelayedResultFunction
import com.lightningkite.kotlinx.httpclient.HttpBody
import com.lightningkite.kotlinx.httpclient.HttpClient
import com.lightningkite.kotlinx.httpclient.HttpMethod
import com.lightningkite.kotlinx.httpclient.HttpResponse
import com.lightningkite.kotlinx.serialization.json.BJson
import com.lightningkite.kotlinx.serialization.json.callJson

inline fun <reified T : Any> ServerFunction<T>.invoke(
        onEndpoint: String,
        headers: Map<String, List<String>> = mapOf()
): DelayedResultFunction<HttpResponse<T>> = HttpClient.callJson(
        url = onEndpoint,
        method = HttpMethod.POST,
        body = HttpBody.BJson(this),
        headers = headers
)