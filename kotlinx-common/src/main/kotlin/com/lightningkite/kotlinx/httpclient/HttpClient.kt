package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.async.DelayedResultFunction

/*
This library should use kotlinx-io

*/

expect object HttpClient{
    fun callString(
            url:String,
            method:HttpMethod,
            body:HttpBody,
            headers:Map<String, List<String>> = mapOf()
    ):DelayedResultFunction<HttpResponse<String>>

    fun callByteArray(
            url:String,
            method:HttpMethod,
            body:HttpBody,
            headers:Map<String, List<String>> = mapOf()
    ):DelayedResultFunction<HttpResponse<ByteArray>>

    fun socket(
            url: String
    ): DelayedResultFunction<SuccessOrFailure<HttpWebSocket>>
}

