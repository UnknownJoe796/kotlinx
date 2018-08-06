package com.lightningkite.kotlinx.serialization.json

/**
 * Manages JSON streaming.
 */
class RawJsonReader(reader: Iterator<Char>) {
    val lexer = Lexer(reader)

    /**
     * @return the next value, whatever it may be.
     * @throws JsonParsingException the next value is not a String.
     */
    fun nextAny(): Any? = consumeValue { it }

    /**
     * @return the next String.
     * @throws JsonParsingException the next value is not a String.
     */
    fun nextString(): String = consumeValue { value ->
        when (value) {
            is String -> value
            else -> value.toString()
        }
    }

    /**
     * @return the next Int.
     * @throws JsonParsingException the next value is not an Int.
     */
    fun nextInt(): Int = consumeValue { value ->
        when (value) {
            is Int -> value
            is Number -> value.toInt()
            is String -> value.toIntOrNull()
                    ?: throw JsonParsingException("Next token is not a int: $value", lexer.line, lexer.index)
            else -> throw JsonParsingException("Next token is not a int: $value", lexer.line, lexer.index)
        }
    }

    /**
     * @return the next Long (upscaling as needed).
     * @throws JsonParsingException the next value is not an Long.
     */
    fun nextLong(): Long = consumeValue { value ->
        when (value) {
            is Int -> value.toLong()
            is Long -> value
            is Number -> value.toLong()
            is String -> value.toLongOrNull()
                    ?: throw JsonParsingException("Next token is not a int: $value", lexer.line, lexer.index)
            else -> throw JsonParsingException("Next token is not a long: $value", lexer.line, lexer.index)
        }
    }

    /**
     * @return the next Double (upscaling as needed).
     * @throws JsonParsingException the next value is not a Double.
     */
    fun nextDouble() = consumeValue { value ->
        when (value) {
            is Double -> value
            is Number -> value.toDouble()
            is String -> value.toDoubleOrNull()
                    ?: throw JsonParsingException("Next token is not a int: $value", lexer.line, lexer.index)
            else -> throw JsonParsingException("Next token is not a double: $value", lexer.line, lexer.index)
        }
    }

    /**
     * @return the next boolean.
     * @throws JsonParsingException the next value is not a Boolean.
     */
    fun nextBoolean() = consumeValue { value ->
        value as? Boolean
                ?: throw JsonParsingException("Next token is not a boolean: $value", lexer.line, lexer.index)
    }

    /**
     * @return the next object, making sure the current token is an open brace and the last token is a closing brace.
     */
    fun nextObject(): HashMap<String, Any?> {
        return beginObject {
            HashMap<String, Any?>().let { result ->
                while (hasNext()) {
                    val name = nextName()
                    val value = consumeValue { value -> value }
                    result[name] = value
                }
                result
            }
        }
    }

    /**
     * @return the next array, making sure the current token is an open bracket and the last token is a closing bracket.
     */
    fun nextArray(): List<Any> {
        return beginArray {
            arrayListOf<Any>().let { result ->
                while (hasNext()) {
                    val v = consumeValue { value -> value }
                    if (v != null) {
                        result.add(v)
                    } else {
                        throw KlaxonException("Couldn't parse")
                    }
                }
                result
            }
        }
    }

    /**
     * @return the next name (the string left of a colon character).
     */
    fun nextName(): String {
        skip()
        val next = lexer.nextToken()
        if (next.tokenType == TokenType.VALUE) {
            return next.value.toString()
        } else {
            throw KlaxonException("Expected a name but got $next")
        }
    }

    /**
     * Make sure that the next token is the beginning of an object (open brace),
     * consume it, run the closure and then make sure the object is closed (closed brace).
     */
    fun <T> beginObject(closure: () -> T): T {
        skip()
        privateBeginObject()
        val result = closure()
        privateEndObject()
        return result
    }

    /**
     * Tun the closure and then make sure the object is closed (closed brace).
     */
    fun <T> midObject(closure: () -> T): T {
        skip()
        val result = closure()
        privateEndObject()
        return result
    }

    /**
     * Makes sure that the next token is the beginning of an array (open bracket),
     * consume it, run the closure and then make sure the array is closed (closed bracket).
     */
    fun <T> beginArray(closure: () -> T): T {
        skip()
        privateBeginArray()
        val result = closure()
        privateEndArray()
        return result
    }

    /**
     * Makes sure that the next token is the beginning of an array (open bracket),
     * consume it, run the closure and then make sure the array is closed (closed bracket).
     */
    fun <T> midArray(closure: () -> T): T {
        skip()
        val result = closure()
        privateEndArray()
        return result
    }

    /**
     * @return true if this reader has more tokens to read before finishing the current object/array.
     */
    fun hasNext(): Boolean = lexer.peek().tokenType.let { it != TokenType.RIGHT_BRACKET && it != TokenType.RIGHT_BRACE }

    private fun consumeToken(type: TokenType, expected: String) {
        val next = lexer.nextToken()
        if (next.tokenType != type) {
            throw KlaxonException("Expected a $expected but read $next")
        }
    }

    private fun privateBeginArray() = consumeToken(TokenType.LEFT_BRACKET, "[")
    private fun privateEndArray() = consumeToken(TokenType.RIGHT_BRACKET, "]")

    private fun privateBeginObject() = consumeToken(TokenType.LEFT_BRACE, "{")
    private fun privateEndObject() = consumeToken(TokenType.RIGHT_BRACE, "}")

    private val SKIPS = setOf(TokenType.COLON, TokenType.COMMA)
    private fun skip() {
        while (SKIPS.contains(lexer.peek().tokenType)) lexer.nextToken()
    }

    private inline fun <T> consumeValue(convert: (Any?) -> T): T {
        skip()

        val next = lexer.peek()
        return when (next.tokenType) {
            TokenType.VALUE -> convert(lexer.nextToken().value)
            TokenType.LEFT_BRACE -> nextObject() as T
            TokenType.LEFT_BRACKET -> nextArray() as T
            else -> throw JsonParsingException("Expected some form of value, got something else", lexer.line, lexer.index)
        }
    }

}