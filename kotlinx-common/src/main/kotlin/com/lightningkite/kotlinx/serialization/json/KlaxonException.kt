package com.lightningkite.kotlinx.serialization.json


open class KlaxonException(s: String) : RuntimeException(s)
class JsonParsingException(s: String, line: Int, index: Int) : RuntimeException("$s at position line $line index $index")