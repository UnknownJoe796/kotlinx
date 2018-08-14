package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.async.DelayedResultFunction
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.TEXT
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType

actual object HttpClient {

    actual fun callString(
            url: String,
            method: HttpMethod,
            body: HttpBody,
            headers: Map<String, List<String>>
    ): DelayedResultFunction<HttpResponse<String>> = { callback ->
        val request = XMLHttpRequest()
        request.responseType = XMLHttpRequestResponseType.TEXT
        request.addEventListener("load", callback = {
            val result = toHttpResponse(request, XMLHttpRequest::responseText, XMLHttpRequest::responseText)
            callback.invoke(result)
        })
        request.addEventListener("error", callback = {
            val result = toHttpResponse(request, XMLHttpRequest::responseText, XMLHttpRequest::responseText)
            callback.invoke(result)
        })
        request.open(method.name, url)
        request.send(when (body) {
            is HttpBody.BString -> body.value
            is HttpBody.BByteArray -> body.value
        })
    }

    actual fun callByteArray(
            url: String,
            method: HttpMethod,
            body: HttpBody,
            headers: Map<String, List<String>>
    ): DelayedResultFunction<HttpResponse<ByteArray>> = { callback ->
        val request = XMLHttpRequest()
        request.responseType = XMLHttpRequestResponseType.ARRAYBUFFER
        request.addEventListener("load", callback = {
            val result = toHttpResponse(request, XMLHttpRequest::responseByteArrayString, XMLHttpRequest::responseByteArray)
            callback.invoke(result)
        })
        request.addEventListener("error", callback = {
            val result = toHttpResponse(request, XMLHttpRequest::responseByteArrayString, XMLHttpRequest::responseByteArray)
            callback.invoke(result)
        })
        request.open(method.name, url)
        request.send(when (body) {
            is HttpBody.BString -> body.value
            is HttpBody.BByteArray -> body.value
        })
    }

    private fun <T> toHttpResponse(
            request: XMLHttpRequest,
            getError: (XMLHttpRequest) -> String,
            getResult: (XMLHttpRequest) -> T
    ): HttpResponse<T> {
        return HttpResponse(
                code = request.status.toInt(),
                headers = request.getAllResponseHeaders()
                        .trim().split("\r\n")
                        .asSequence()
                        .mapNotNull {
                            val splitPoint = it.indexOf(':')
                            if (splitPoint == -1) return@mapNotNull null
                            it.substring(0, splitPoint) to it.substring(splitPoint + 2)
                        }
                        .groupingBy { it.first }
                        .fold(
                                initialValueSelector = { _, _ -> ArrayList<String>() },
                                operation = { key: String, acc: ArrayList<String>, ele: Pair<String, String> ->
                                    acc.apply { add(ele.second) }
                                }
                        ),
                result = if (request.status % 200 == 2) {
                    SuccessOrFailure.success(getResult(request))
                } else {
                    SuccessOrFailure.failure(HttpException(getError(request)))
                }
        )
    }


    actual fun socket(url: String): DelayedResultFunction<SuccessOrFailure<HttpWebSocket>> = { callback ->
        try {
            HttpWebSocketImpl(url, callback)
        } catch (e: Exception) {
            println(e.message)
            callback.invoke(SuccessOrFailure.failure(e))
        }
    }
}

private fun XMLHttpRequest.responseByteArray(): ByteArray {
    val base = Int8Array(response as ArrayBuffer)
    return ByteArray(base.length) { base[it] }
}

private fun XMLHttpRequest.responseByteArrayString(): String {
    val buffer = response as ArrayBuffer
    val array = Uint8Array(buffer)
    val builder = StringBuilder(capacity = buffer.byteLength)
    for (i in 0 until array.length) {
        builder.append(array[i].toChar())
    }
    return builder.toString()
}