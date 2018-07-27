package com.lightningkite.kotlinx.serialization.json

import kotlin.test.Test
import kotlin.test.assertEquals

class JsonWriterTest{

    @Test
    fun test(){
        val expected = """{"int":42,"double":1.234,"string":"Some String\nMultiline","boolean":true,"list":[1,2,3]}"""
        val output = StringBuilder().apply{
            json {
                writeObject {
                    writeEntry("int"){
                        writeNumber(42)
                    }
                    writeEntry("double"){
                        writeNumber(1.234)
                    }
                    writeEntry("string"){
                        writeString("Some String\nMultiline")
                    }
                    writeEntry("boolean"){
                        writeBoolean(true)
                    }
                    writeEntry("boolean"){
                        writeBoolean(true)
                    }
                    writeEntry("list"){
                        writeArray {
                            writeEntry { writeNumber(1) }
                            writeEntry { writeNumber(2) }
                            writeEntry { writeNumber(3) }
                        }
                    }
                }
            }
        }.toString()
        println(output)
        assertEquals(expected, output)
    }
}