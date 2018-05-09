package com.lightningkite.kotlinx.httpclient

sealed class HttpResponse<T> {
    abstract val code:Int
    abstract val headers:Map<String, List<String>>

    class Success<T>(
            override val code: Int,
            override val headers: Map<String, List<String>>,
            val result:T
    ): HttpResponse<T>()

    class Failure<T>(
            override val code: Int,
            override val headers: Map<String, List<String>>,
            val message:String,
            val exception:Exception? = null
    ): HttpResponse<T>()
}

inline fun <I, O> HttpResponse<I>.copy(convert:(I)->O):HttpResponse<O>{
    return when(this){
        is HttpResponse.Success<I> -> HttpResponse.Success<O>(
                code = code,
                headers = headers,
                result = convert(result)
        )
        is HttpResponse.Failure<I> -> HttpResponse.Failure<O>(
                code = code,
                headers = headers,
                message = message,
                exception = exception
        )
    }
}