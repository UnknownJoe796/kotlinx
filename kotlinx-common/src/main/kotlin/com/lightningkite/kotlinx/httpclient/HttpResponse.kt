package com.lightningkite.kotlinx.httpclient

data class HttpResponse<T>(
        val code: Int,
        val headers: Map<String, List<String>>,
        val result: SuccessOrFailure<T>
) {
    companion object {
        fun <T> failure(exception: Throwable) = HttpResponse<T>(0, mapOf(), SuccessOrFailure.failure(exception))
    }
}

class HttpException(message: String) : Exception(message)

inline fun <I, O> HttpResponse<I>.copy(convert:(I)->O):HttpResponse<O>{
    return HttpResponse(code = code, headers = headers, result = result.mapCatching(convert))
}