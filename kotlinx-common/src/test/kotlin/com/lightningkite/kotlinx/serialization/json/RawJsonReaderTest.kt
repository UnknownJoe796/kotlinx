package com.lightningkite.kotlinx.serialization.json

import kotlin.test.Test

class RawJsonReaderTest {

    @Test
    fun test() {
        val reading = """{"int":42,"double":1.234,"string":"Some String\nMultiline","boolean":true,"list":[1,2,3]}"""
        RawJsonReader(reading.iterator()).apply {
            val obj = nextObject()
            println(obj)
        }
    }
}