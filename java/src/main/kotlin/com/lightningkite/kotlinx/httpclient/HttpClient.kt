package com.lightningkite.kotlinx.httpclient

import com.lightningkite.kotlinx.async.DelayedResultFunction
import com.lightningkite.kotlinx.httpclient.HttpClient.toKotlin
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
            HttpResponse.Success(
                    code = code(),
                    headers = headers().toMultimap(),
                    result = read()
            )
        } else {
            HttpResponse.Failure(
                    code = code(),
                    headers = headers().toMultimap(),
                    message = body()?.string() ?: ""
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
                    callback.invoke(HttpResponse.Failure<String>(
                            code = 0,
                            headers = mapOf(),
                            message = e.message ?: "",
                            exception = e as? Exception
                    ))
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
                    callback.invoke(HttpResponse.Failure<ByteArray>(
                            code = 0,
                            headers = mapOf(),
                            message = e.message ?: "",
                            exception = e as? Exception
                    ))
                }
            }

            override fun onResponse(call: Call, response: Response) {
                resultThread.invoke {
                    callback.invoke(response.toKotlin{ body()!!.bytes() })
                }
            }

        })
    }
}