package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.bytes.toHexString

inline fun <T: Appendable> T.json(write:JsonWriter.()->Unit): T {
    JsonWriter(this).apply(write)
    return this
}

class JsonWriter(val raw: Appendable) {

    @Suppress("NOTHING_TO_INLINE")
    inline fun writeNumber(number: Number) {
        raw.append(number.toString())
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun writeBoolean(boolean: Boolean) {
        raw.append(boolean.toString())
    }

    @Suppress("NOTHING_TO_INLINE")
    inline fun writeNull() {
        raw.append("null")
    }

    fun writeString(string: String) {
        raw.append('\"')
        for (ch in string) {
            when (ch) {
                '"' -> raw.append("\\").append(ch)
                '\\' -> raw.append(ch).append(ch)
                '\n' -> raw.append("\\n")
                '\r' -> raw.append("\\r")
                '\t' -> raw.append("\\t")
                '\b' -> raw.append("\\b")
                '\u000c' -> raw.append("\\f")
                in '\u0000'..'\u001F',
                in '\u007F'..'\u009F',
                in '\u2000'..'\u20FF' -> {
                    raw.append("\\u")
                    raw.append(ch.toInt().toHexString().padStart(4, '0'))
                }
                else -> raw.append(ch)
            }
        }
        raw.append('\"')
    }

    inner class ObjectWriter() {
        var alreadyWrittenOne = false

        inline fun writeEntry(name: String, writeValue: JsonWriter.() -> Unit) {
            if (alreadyWrittenOne) {
                raw.append(',')
            }
            writeString(name)
            raw.append(':')
            this@JsonWriter.writeValue()
            alreadyWrittenOne = true
        }
    }

    inline fun writeObject(middle: ObjectWriter.() -> Unit) {
        raw.append('{')
        ObjectWriter().middle()
        raw.append('}')
    }

    inner class ArrayWriter() {
        var alreadyWrittenOne = false

        inline fun writeEntry(writeValue: JsonWriter.() -> Unit) {
            if (alreadyWrittenOne) {
                raw.append(',')
            }
            this@JsonWriter.writeValue()
            alreadyWrittenOne = true
        }
    }

    inline fun writeArray(middle: ArrayWriter.() -> Unit) {
        raw.append('[')
        ArrayWriter().middle()
        raw.append(']')
    }
}