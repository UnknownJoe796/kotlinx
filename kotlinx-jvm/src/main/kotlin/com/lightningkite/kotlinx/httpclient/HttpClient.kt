package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.async.DelayedResultFunction
import okhttp3.*
import java.io.IOException

actual object HttpClient {

    var resultThread: (() -> Unit) -> Unit = { it.invoke() }
    val okClient = OkHttpClient()

    fun HttpBody.toOk(method:HttpMethod): RequestBody? {
        if(method == HttpMethod.GET) return null
        return when (this) {
            is HttpBody.BByteArray -> {
                RequestBody.create(MediaType.parse(contentType), this.value)
            }
            is HttpBody.BString -> {
                RequestBody.create(MediaType.parse(contentType), this.value)
            }
        }
    }

    fun <T> Response.toKotlin(read: Response.() -> T): HttpResponse<T> {
        return if (code() / 100 == 2) {
            HttpResponse(
                    code = code(),
                    headers = headers().toMultimap(),
                    result = SuccessOrFailure.success(read())
            )
        } else {
            HttpResponse(
                    code = code(),
                    headers = headers().toMultimap(),
                    result = SuccessOrFailure.failure(HttpException(body()?.string() ?: ""))
            )
        }
    }

    actual fun callString(
            url: String,
            method: HttpMethod,
            body: HttpBody,
            headers: Map<String, List<String>>
    ): DelayedResultFunction<HttpResponse<String>> = { callback ->
        val rq = Request.Builder()
                .url(url)
                .method(method.name, body.toOk(method))
                .build()
        okClient.newCall(rq).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                resultThread.invoke {
                    callback.invoke(HttpResponse.failure(e))
                }
            }

            override fun onResponse(call: Call, response: Response) {
                resultThread.invoke {
                    callback.invoke(response.toKotlin{ body()!!.string() })
                }
            }

        })
    }

    actual fun callByteArray(
            url: String,
            method: HttpMethod,
            body: HttpBody,
            headers: Map<String, List<String>>
    ): DelayedResultFunction<HttpResponse<ByteArray>> = { callback ->
        val rq = Request.Builder()
                .url(url)
                .method(method.name, body.toOk(method))
                .build()
        okClient.newCall(rq).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                resultThread.invoke {
                    callback.invoke(HttpResponse.failure(e))
                }
            }

            override fun onResponse(call: Call, response: Response) {
                resultThread.invoke {
                    callback.invoke(response.toKotlin{ body()!!.bytes() })
                }
            }

        })
    }

    actual fun socket(url: String): DelayedResultFunction<SuccessOrFailure<HttpWebSocket>> = { callback ->
        try {
            val handler = HttpWebSocketHandler()
            val underlying = HttpClient.okClient.newWebSocket(
                    Request.Builder().url(url).build(),
                    handler
            )
            handler.init(underlying, callback)
        } catch (e: Exception) {
            e.printStackTrace()
            callback.invoke(SuccessOrFailure.failure(e))
        }
    }
}