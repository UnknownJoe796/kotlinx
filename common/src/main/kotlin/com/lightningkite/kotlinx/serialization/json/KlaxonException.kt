package com.lightningkite.kotlinx.serialization.json


open class KlaxonException(s: String) : RuntimeException(s)
class JsonParsingException(s: String) : KlaxonException(s)