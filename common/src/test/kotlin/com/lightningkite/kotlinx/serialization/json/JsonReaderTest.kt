package com.lightningkite.kotlinx.serialization.json

import kotlin.test.Test
import kotlin.test.assertEquals

class JsonReaderTest{

    @Test
    fun test(){
        val reading = """{"int":42,"double":1.234,"string":"Some String\nMultiline","boolean":true,"list":[1,2,3]}"""
        JsonReader(reading.iterator()).apply {
            val obj = nextObject()
            println(obj)
        }
    }
}