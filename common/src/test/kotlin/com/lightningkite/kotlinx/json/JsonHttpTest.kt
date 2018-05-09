package com.lightningkite.kotlinx.json

import kotlinx.serialization.Serializable
import kotlin.test.Test
import com.lightningkite.kotlinx.httpclient.HttpClient
import com.lightningkite.kotlinx.httpclient.HttpMethod
import kotlinx.serialization.internal.UnitSerializer
import kotlinx.serialization.list
import kotlinx.serialization.serializer

class JsonHttpTest{

    @Serializable
    data class FillText(
            var fname:String = "",
            var lname:String = ""
    )

    @Test fun test(){
        var called = false
        HttpClient.callJson(
                url = "http://www.filltext.com/?rows=10&fname={firstName}&lname={lastName}&pretty=true",
                method = HttpMethod.GET,
                body = Unit,
                deserializer = FillText::class.serializer().list
        ).invoke {
            println("JsonHttpTest: $it")
            called = true
        }
    }
}