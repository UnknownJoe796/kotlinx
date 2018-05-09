package com.lightningkite.kotlinx.httpclient

sealed class HttpResponse<T> {
    abstract val code:Int
    abstract val headers:Map<String, List<String>>

    data class Success<T>(
            override val code: Int,
            override val headers: Map<String, List<String>>,
            val result:T
    ): HttpResponse<T>()

    data class Failure<T>(
            override val code: Int,
            override val headers: Map<String, List<String>>,
            val message:String,
            val exception:Exception? = null
    ): HttpResponse<T>()
}

inline fun <I, O> HttpResponse<I>.copy(convert:(I)->O):HttpResponse<O>{
    return when(this){
        is HttpResponse.Success<I> -> {
            try{
                val transformed = convert(result)
                HttpResponse.Success<O>(
                        code = code,
                        headers = headers,
                        result = transformed
                )
            } catch(e:Exception){
                HttpResponse.Failure<O>(
                        code = 0,
                        headers = headers,
                        message = e.message ?: "",
                        exception = e
                )
            }
        }
        is HttpResponse.Failure<I> -> HttpResponse.Failure<O>(
                code = code,
                headers = headers,
                message = message,
                exception = exception
        )
    }
}