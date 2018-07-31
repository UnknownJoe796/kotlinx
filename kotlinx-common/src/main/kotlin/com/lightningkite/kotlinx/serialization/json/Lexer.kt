package com.lightningkite.kotlinx.serialization.json

import com.lightningkite.kotlinx.string.isDigit

enum class TokenType(val value: String) {
    VALUE("a value"),
    LEFT_BRACE("\"{\""),
    RIGHT_BRACE("\"}\""),
    LEFT_BRACKET("\"[\""),
    RIGHT_BRACKET("\"]\""),
    COMMA("\",\""),
    COLON("\":\""),
    EOF("EOF")
}

data class Token(val tokenType: TokenType, val value: Any? = null) {
    override fun toString(): String {
        val v =
                if (value != null) {
                    " ($value)"
                } else {
                    ""
                }
        return tokenType.toString() + v
    }
}

@Suppress("NOTHING_TO_INLINE")
inline fun <T : Any> Iterator<T>.nextOrNull(): T? = if (hasNext()) next() else null

class Lexer(val reader: Iterator<Char>) : Iterator<Token> {
    private val EOF = Token(TokenType.EOF, null)
    var index = 0
    var line = 1

    private val NUMERIC = Regex("[-]?[0-9]+")
    private val DOUBLE = Regex(NUMERIC.toString() + "((\\.[0-9]+)?([eE][-+]?[0-9]+)?)")

    private fun isSpace(c: Char): Boolean {
        if (c == '\n') line++
        return c == ' ' || c == '\r' || c == '\n' || c == '\t'
    }

    private var next: Char?

    init {
        next = reader.nextOrNull()
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun nextChar(): Char {
        val c = next ?: throw IllegalStateException("Cannot get next char: EOF reached")
        next = reader.nextOrNull()
        index++
        return c
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun peekChar(): Char {
        return next ?: throw IllegalStateException("Cannot peek next char: EOF reached")
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun isDone(): Boolean = next == null

    val BOOLEAN_LETTERS = "falsetrue".toSet()
    private fun isBooleanLetter(c: Char): Boolean {
        return BOOLEAN_LETTERS.contains(c.toLowerCase())
    }

    val NULL_LETTERS = "null".toSet()

    fun isValueLetter(c: Char): Boolean {
        return c == '-' || c == '+' || c == '.' || c.isDigit() || isBooleanLetter(c)
                || c in NULL_LETTERS
    }

    private var peeked: Token? = null

    fun peek(): Token {
        if (peeked == null) {
            peeked = actualNextToken()
        }
        return peeked!!
    }

    override fun next() = nextToken()
    override fun hasNext() = peek() != EOF

    fun nextToken(): Token {
        val result =
                if (peeked != null) {
                    val r = peeked!!
                    peeked = null
                    r
                } else {
                    actualNextToken()
                }
        return result
    }

    private var expectName = false

    private fun actualNextToken(): Token {

        if (isDone()) {
            return EOF
        }

        val tokenType: TokenType
        var c = nextChar()
        val currentValue = StringBuilder()
        var jsonValue: Any? = null

        while (!isDone() && isSpace(c)) {
            c = nextChar()
        }

        when {
            '"' == c -> {
                tokenType = TokenType.VALUE
                loop@
                do {
                    if (isDone()) {
                        throw KlaxonException("Unterminated string")
                    }

                    c = nextChar()
                    when (c) {
                        '\\' -> {
                            if (isDone()) {
                                throw KlaxonException("Unterminated string")
                            }

                            c = nextChar()
                            when (c) {
                                '\\' -> currentValue.append("\\")
                                '/' -> currentValue.append("/")
                                'b' -> currentValue.append("\b")
                                'f' -> currentValue.append("\u000c")
                                'n' -> {
                                    currentValue.append("\n")
                                }
                                'r' -> currentValue.append("\r")
                                't' -> currentValue.append("\t")
                                'u' -> {
                                    val unicodeChar = StringBuilder(4)
                                            .append(nextChar())
                                            .append(nextChar())
                                            .append(nextChar())
                                            .append(nextChar())
                                    val intValue = unicodeChar.toString().toInt(16)
                                    currentValue.append(intValue.toChar())
                                }
                                else -> currentValue.append(c)
                            }
                        }
                        '"' -> break@loop
                        else -> currentValue.append(c)
                    }
                } while (true)

                jsonValue = currentValue.toString()
            }
            '{' == c -> {
                tokenType = TokenType.LEFT_BRACE
                expectName = true
            }
            '}' == c -> {
                tokenType = TokenType.RIGHT_BRACE
                expectName = false
            }
            '[' == c -> {
                tokenType = TokenType.LEFT_BRACKET
                expectName = false
            }
            ']' == c -> {
                tokenType = TokenType.RIGHT_BRACKET
                expectName = false
            }
            ':' == c -> {
                tokenType = TokenType.COLON
                expectName = false
            }
            ',' == c -> {
                tokenType = TokenType.COMMA
                expectName = true
            }
            !isDone() -> {
                while (isValueLetter(c)) {
                    currentValue.append(c)
                    if (!isValueLetter(peekChar())) {
                        break
                    } else {
                        c = nextChar()
                    }
                }
                val v = currentValue.toString()
                jsonValue = when {
                    NUMERIC.matches(v) -> try {
                        v.toInt()
                    } catch (e: NumberFormatException) {
                        v.toLong()
                        //TODO: Bigger numbers
                    }
                    DOUBLE.matches(v) -> v.toDouble()
                    "true" == v.toLowerCase() -> true
                    "false" == v.toLowerCase() -> false
                    v == "null" -> null
                    else -> throw KlaxonException("Unexpected character at position ${index - 1}"
                            + ": '$c' (ASCII: ${c.toInt()})'")
                }

                tokenType = TokenType.VALUE
            }
            else -> tokenType = TokenType.EOF
        }

        return Token(tokenType, jsonValue)
    }
}