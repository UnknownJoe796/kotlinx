package com.lightningkite.kotlinx.httpclient

sealed class HttpBody{
    abstract val contentType:String
    data class BString(override val contentType: String, val value:String): HttpBody()
    class BByteArray(override val contentType: String, val value:ByteArray): HttpBody()
}