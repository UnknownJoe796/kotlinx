package com.lightningkite.kotlinx.serialization

import java.io.Reader

fun Reader.iterator(): Iterator<Char> = object : Iterator<Char> {

    var queuedUsed: Boolean = false
    var queued: Int = 0

    override fun hasNext(): Boolean {
        if (queuedUsed) {
            queued = read()
        }
        return queued != -1
    }

    override fun next(): Char {
        if (queuedUsed) {
            queued = read()
        }
        queuedUsed = true
        return queued.toChar()
    }
}