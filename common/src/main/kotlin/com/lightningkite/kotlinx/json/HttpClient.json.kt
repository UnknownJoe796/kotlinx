package com.lightningkite.kotlinx.json

import com.lightningkite.kotlinx.async.DelayedResultFunction
import com.lightningkite.kotlinx.async.transform
import com.lightningkite.kotlinx.httpclient.*
import kotlinx.serialization.json.JSON

inline fun <reified I: Any, reified O: Any> HttpClient.callJson(
        url:String,
        method: HttpMethod,
        body:I,
        headers:Map<String, List<String>> = mapOf(),
        json: JSON = MyJson
):DelayedResultFunction<HttpResponse<O>> = callString(
        url,
        method,
        HttpBody.BString(HttpContentTypes.Application.Json, json.stringify(body)),
        headers
).transform { it.copy { json.parse<O>(it) } }