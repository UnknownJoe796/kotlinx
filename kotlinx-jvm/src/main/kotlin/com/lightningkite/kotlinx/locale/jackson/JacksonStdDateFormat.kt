package com.lightningkite.kotlinx.locale.jackson

import java.text.*
import java.util.*
import java.util.regex.Pattern

/**
 * Default [DateFormat] implementation used by standard Date
 * serializers and deserializers. For serialization defaults to using
 * an ISO-8601 compliant format (format String "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
 * and for deserialization, both ISO-8601 and RFC-1123.
 */
class JacksonStdDateFormat : DateFormat {

    /**
     * Caller may want to explicitly override timezone to use; if so,
     * we will have non-null value here.
     */
    @Transient
    protected var _timezone: TimeZone? = null

    protected val _locale: Locale

    /**
     * Explicit override for leniency, if specified.
     *
     *
     * Cannot be `final` because [.setLenient] returns
     * `void`.
     */
    protected var _lenient: Boolean? = null

    /**
     * Lazily instantiated calendar used by this instance for serialization ([.format]).
     *
     * @since 2.9.1
     */
    @Transient
    private var _calendar: Calendar? = null

    @Transient
    private var _formatRFC1123: DateFormat? = null

    /**
     * Whether the TZ offset must be formatted with a colon between hours and minutes (`HH:mm` format)
     *
     *
     * NOTE: default changed to `true` in Jackson 3.0; was `false` earlier.
     */
    protected var _tzSerializedWithColon = true

    constructor() {
        _locale = DEFAULT_LOCALE
    }

    /**
     * @since 2.9.1
     */
    @JvmOverloads protected constructor(tz: TimeZone?, loc: Locale, lenient: Boolean?,
                                        formatTzOffsetWithColon: Boolean = false) {
        _timezone = tz
        _locale = loc
        _lenient = lenient
        _tzSerializedWithColon = formatTzOffsetWithColon
    }

    /**
     * Method used for creating a new instance with specified timezone;
     * if no timezone specified, defaults to the default timezone (UTC).
     */
    fun withTimeZone(tz: TimeZone?): JacksonStdDateFormat {
        var tz = tz
        if (tz == null) {
            tz = defaultTimeZone
        }
        return if (tz === _timezone || tz == _timezone) {
            this
        } else JacksonStdDateFormat(tz, _locale, _lenient, _tzSerializedWithColon)
    }

    /**
     * "Mutant factory" method that will return an instance that uses specified
     * `Locale`:
     * either `this` instance (if setting would not change), or newly
     * constructed instance with different `Locale` to use.
     */
    fun withLocale(loc: Locale): JacksonStdDateFormat {
        return if (loc == _locale) {
            this
        } else JacksonStdDateFormat(_timezone, loc, _lenient, _tzSerializedWithColon)
    }

    /**
     * "Mutant factory" method that will return an instance that has specified leniency
     * setting: either `this` instance (if setting would not change), or newly
     * constructed instance.
     *
     * @since 2.9
     */
    fun withLenient(b: Boolean?): JacksonStdDateFormat {
        return if (_equals(b, _lenient)) {
            this
        } else JacksonStdDateFormat(_timezone, _locale, b, _tzSerializedWithColon)
    }

    /**
     * "Mutant factory" method that will return an instance that has specified
     * handling of colon when serializing timezone (timezone either written
     * like `+0500` or `+05:00`):
     * either `this` instance (if setting would not change), or newly
     * constructed instance with desired setting for colon inclusion.
     *
     *
     * NOTE: does NOT affect deserialization as colon is optional accepted
     * but not required -- put another way, either serialization is accepted
     * by this class.
     *
     * @since 2.9.1
     */
    fun withColonInTimeZone(b: Boolean): JacksonStdDateFormat {
        return if (_tzSerializedWithColon == b) {
            this
        } else JacksonStdDateFormat(_timezone, _locale, _lenient, b)
    }

    override fun clone(): JacksonStdDateFormat {
        // Although there is that much state to share, we do need to
        // orchestrate a bit, mostly since timezones may be changed
        return JacksonStdDateFormat(_timezone, _locale, _lenient, _tzSerializedWithColon)
    }

    override// since 2.6
    fun getTimeZone(): TimeZone? {
        return _timezone
    }

    override fun setTimeZone(tz: TimeZone) {
        /* DateFormats are timezone-specific (via Calendar contained),
         * so need to reset instances if timezone changes:
         */
        if (tz != _timezone) {
            _clearFormats()
            _timezone = tz
        }
    }

    /**
     * Need to override since we need to keep track of leniency locally,
     * and not via underlying [Calendar] instance like base class
     * does.
     */
    override// since 2.7
    fun setLenient(enabled: Boolean) {
        val newValue = java.lang.Boolean.valueOf(enabled)
        if (!_equals(newValue, _lenient)) {
            _lenient = newValue
            // and since leniency settings may have been used:
            _clearFormats()
        }
    }

    override// since 2.7
    fun isLenient(): Boolean {
        // default is, I believe, true
        return _lenient == null || _lenient!!
    }

    @Throws(ParseException::class)
    override fun parse(dateStr: String): Date {
        var dateStr = dateStr
        dateStr = dateStr.trim { it <= ' ' }
        val pos = ParsePosition(0)
        val dt = _parseDate(dateStr, pos)
        if (dt != null) {
            return dt
        }
        val sb = StringBuilder()
        for (f in ALL_FORMATS) {
            if (sb.length > 0) {
                sb.append("\", \"")
            } else {
                sb.append('"')
            }
            sb.append(f)
        }
        sb.append('"')
        throw ParseException(String.format("Cannot parse date \"%s\": not compatible with any of standard forms (%s)",
                dateStr, sb.toString()), pos.errorIndex)
    }

    // 24-Jun-2017, tatu: I don't think this ever gets called. So could... just not implement?
    override fun parse(dateStr: String, pos: ParsePosition): Date? {
        try {
            return _parseDate(dateStr, pos)
        } catch (e: ParseException) {
            // may look weird but this is what `DateFormat` suggest to do...
        }

        return null
    }

    @Throws(ParseException::class)
    protected fun _parseDate(dateStr: String, pos: ParsePosition): Date? {
        if (looksLikeISO8601(dateStr)) { // also includes "plain"
            return parseAsISO8601(dateStr, pos)
        }
        // Also consider "stringified" simple time stamp
        var i = dateStr.length
        while (--i >= 0) {
            val ch = dateStr[i]
            if (ch < '0' || ch > '9') {
                // 07-Aug-2013, tatu: And [databind#267] points out that negative numbers should also work
                if (i > 0 || ch != '-') {
                    break
                }
            }
        }
        return if (i < 0
                // let's just assume negative numbers are fine (can't be RFC-1123 anyway); check length for positive
                && dateStr[0] == '-') {
            _parseDateFromLong(dateStr, pos)
        } else parseAsRFC1123(dateStr, pos)
        // Otherwise, fall back to using RFC 1123. NOTE: call will NOT throw, just returns `null`
    }

    override fun format(date: Date, toAppendTo: StringBuffer,
                        fieldPosition: FieldPosition): StringBuffer {
        var tz = _timezone
        if (tz == null) {
            tz = defaultTimeZone
        }
        _format(tz, _locale, date, toAppendTo)
        return toAppendTo
    }

    protected fun _format(tz: TimeZone, loc: Locale, date: Date,
                          buffer: StringBuffer) {
        val cal = _getCalendar(tz)
        cal.time = date

        pad4(buffer, cal.get(Calendar.YEAR))
        buffer.append('-')
        pad2(buffer, cal.get(Calendar.MONTH) + 1)
        buffer.append('-')
        pad2(buffer, cal.get(Calendar.DAY_OF_MONTH))
        buffer.append('T')
        pad2(buffer, cal.get(Calendar.HOUR_OF_DAY))
        buffer.append(':')
        pad2(buffer, cal.get(Calendar.MINUTE))
        buffer.append(':')
        pad2(buffer, cal.get(Calendar.SECOND))
        buffer.append('.')
        pad3(buffer, cal.get(Calendar.MILLISECOND))

        val offset = tz.getOffset(cal.timeInMillis)
        if (offset != 0) {
            val hours = Math.abs(offset / (60 * 1000) / 60)
            val minutes = Math.abs(offset / (60 * 1000) % 60)
            buffer.append(if (offset < 0) '-' else '+')
            pad2(buffer, hours)
            if (_tzSerializedWithColon) {
                buffer.append(':')
            }
            pad2(buffer, minutes)
        } else {
            // 24-Jun-2017, tatu: While `Z` would be conveniently short, older specs
            //   mandate use of full `+0000`
            //            formatted.append('Z');
            if (_tzSerializedWithColon) {
                buffer.append("+00:00")
            } else {
                buffer.append("+0000")
            }
        }
    }

    override fun toString(): String {
        return String.format("DateFormat %s: (timezone: %s, locale: %s, lenient: %s)",
                javaClass.name, _timezone, _locale, _lenient)
    }

    fun toPattern(): String { // same as SimpleDateFormat
        val sb = StringBuilder(100)
        sb.append("[one of: '")
                .append(DATE_FORMAT_STR_ISO8601)
                .append("', '")
                .append(DATE_FORMAT_STR_RFC1123)
                .append("' (")
        sb.append(if (java.lang.Boolean.FALSE == _lenient)
            "strict"
        else
            "lenient")
                .append(")]")
        return sb.toString()
    }

    override fun equals(o: Any?): Boolean {
        return o === this
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }


    /**
     * Helper method used to figure out if input looks like valid
     * ISO-8601 string.
     */
    protected fun looksLikeISO8601(dateStr: String): Boolean {
        return if (dateStr.length >= 7 // really need 10, but...

                && Character.isDigit(dateStr[0])
                && Character.isDigit(dateStr[3])
                && dateStr[4] == '-'
                && Character.isDigit(dateStr[5])) {
            true
        } else false
    }

    @Throws(ParseException::class)
    private fun _parseDateFromLong(longStr: String, pos: ParsePosition): Date {
        val ts: Long
        try {
            ts = java.lang.Long.parseLong(longStr)
        } catch (e: NumberFormatException) {
            throw ParseException(String.format(
                    "Timestamp value %s out of 64-bit value range", longStr),
                    pos.errorIndex)
        }

        return Date(ts)
    }

    @Throws(ParseException::class)
    protected fun parseAsISO8601(dateStr: String, pos: ParsePosition): Date {
        try {
            return _parseAsISO8601(dateStr, pos)
        } catch (e: IllegalArgumentException) {
            throw ParseException(String.format("Cannot parse date \"%s\", problem: %s",
                    dateStr, e.message),
                    pos.errorIndex)
        }

    }

    @Throws(IllegalArgumentException::class, ParseException::class)
    protected fun _parseAsISO8601(dateStr: String, bogus: ParsePosition): Date {
        val totalLen = dateStr.length
        // actually, one short-cut: if we end with "Z", must be UTC
        var tz = defaultTimeZone
        if (_timezone != null && 'Z' != dateStr[totalLen - 1]) {
            tz = _timezone!!
        }
        val cal = _getCalendar(tz)
        cal.clear()
        val formatStr: String
        if (totalLen <= 10) {
            val m = PATTERN_PLAIN.matcher(dateStr)
            if (m.matches()) {
                val year = _parse4D(dateStr, 0)
                val month = _parse2D(dateStr, 5) - 1
                val day = _parse2D(dateStr, 8)

                cal.set(year, month, day, 0, 0, 0)
                cal.set(Calendar.MILLISECOND, 0)
                return cal.time
            }
            formatStr = DATE_FORMAT_STR_PLAIN
        } else {
            val m = PATTERN_ISO8601.matcher(dateStr)
            if (m.matches()) {
                // Important! START with optional time zone; otherwise Calendar will explode

                var start = m.start(2)
                var end = m.end(2)
                val len = end - start
                if (len > 1) { // 0 -> none, 1 -> 'Z'
                    // NOTE: first char is sign; then 2 digits, then optional colon, optional 2 digits
                    var offsetSecs = _parse2D(dateStr, start + 1) * 3600 // hours
                    if (len >= 5) {
                        offsetSecs += _parse2D(dateStr, end - 2) * 60 // minutes
                    }
                    if (dateStr[start] == '-') {
                        offsetSecs *= -1000
                    } else {
                        offsetSecs *= 1000
                    }
                    cal.set(Calendar.ZONE_OFFSET, offsetSecs)
                    // 23-Jun-2017, tatu: Not sure why, but this appears to be needed too:
                    cal.set(Calendar.DST_OFFSET, 0)
                }

                val year = _parse4D(dateStr, 0)
                val month = _parse2D(dateStr, 5) - 1
                val day = _parse2D(dateStr, 8)

                // So: 10 chars for date, then `T`, so starts at 11
                val hour = _parse2D(dateStr, 11)
                val minute = _parse2D(dateStr, 14)

                // Seconds are actually optional... so
                val seconds: Int
                if (totalLen > 16 && dateStr[16] == ':') {
                    seconds = _parse2D(dateStr, 17)
                } else {
                    seconds = 0
                }
                cal.set(year, month, day, hour, minute, seconds)

                // Optional milliseconds
                start = m.start(1) + 1
                end = m.end(1)
                var msecs = 0
                if (start >= end) { // no fractional
                    cal.set(Calendar.MILLISECOND, 0)
                } else {
                    // first char is '.', but rest....
                    msecs = 0
                    val fractLen = end - start
                    when (fractLen) {
                    // fall through
                        3 -> {
                            msecs += dateStr[start + 2] - '0'
                            msecs += 10 * (dateStr[start + 1] - '0')
                            msecs += 100 * (dateStr[start] - '0')
                        }
                        2 -> {
                            msecs += 10 * (dateStr[start + 1] - '0')
                            msecs += 100 * (dateStr[start] - '0')
                        }
                        1 -> msecs += 100 * (dateStr[start] - '0')
                        0 -> {
                        }
                        else // [databind#1745] Allow longer fractions... for now, cap at nanoseconds tho
                        -> {

                            if (fractLen > 9) { // only allow up to nanos
                                throw ParseException(String.format(
                                        "Cannot parse date \"%s\": invalid fractional seconds '%s'; can use at most 9 digits",
                                        dateStr, m.group(1).substring(1)
                                ), start)
                            }
                            msecs += dateStr[start + 2] - '0'
                            msecs += 10 * (dateStr[start + 1] - '0')
                            msecs += 100 * (dateStr[start] - '0')
                        }
                    }
                    cal.set(Calendar.MILLISECOND, msecs)
                }
                return cal.time
            }
            formatStr = DATE_FORMAT_STR_ISO8601
        }

        throw ParseException(String.format("Cannot parse date \"%s\": while it seems to fit format '%s', parsing fails (leniency? %s)",
                dateStr, formatStr, _lenient),
                // [databind#1742]: Might be able to give actual location, some day, but for now
                //  we can't give anything more indicative
                0)
    }

    protected fun parseAsRFC1123(dateStr: String, pos: ParsePosition): Date {
        if (_formatRFC1123 == null) {
            _formatRFC1123 = _cloneFormat(DATE_FORMAT_RFC1123, DATE_FORMAT_STR_RFC1123,
                    _timezone, _locale, _lenient)
        }
        return _formatRFC1123!!.parse(dateStr, pos)
    }

    protected fun _clearFormats() {
        _formatRFC1123 = null
    }

    protected fun _getCalendar(tz: TimeZone): Calendar {
        var cal = _calendar
        if (cal == null) {
            cal = CALENDAR.clone() as Calendar
            _calendar = cal
        }
        if (cal.timeZone != tz) {
            cal.timeZone = tz
        }
        cal.isLenient = isLenient
        return cal
    }

    companion object {
        /* 24-Jun-2017, tatu: Finally rewrote deserialization to use basic Regex
     *   instead of SimpleDateFormat; partly for better concurrency, partly
     *   for easier enforcing of specific rules. Heavy lifting done by Calendar,
     *   anyway.
     */
        protected val PATTERN_PLAIN_STR = "\\d\\d\\d\\d[-]\\d\\d[-]\\d\\d"

        protected val PATTERN_PLAIN = Pattern.compile(PATTERN_PLAIN_STR)

        protected val PATTERN_ISO8601: Pattern

        init {
            var p: Pattern? = null
            try {
                p = Pattern.compile(PATTERN_PLAIN_STR
                        + "[T]\\d\\d[:]\\d\\d(?:[:]\\d\\d)?" // hours, minutes, optional seconds

                        + "(\\.\\d+)?" // optional second fractions

                        + "(Z|[+-]\\d\\d(?:[:]?\\d\\d)?)?" // optional timeoffset/Z
                )
            } catch (t: Throwable) {
                throw RuntimeException(t)
            }

            PATTERN_ISO8601 = p
        }

        /**
         * Defines a commonly used date format that conforms
         * to ISO-8601 date formatting standard, when it includes basic undecorated
         * timezone definition.
         */
        val DATE_FORMAT_STR_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

        /**
         * ISO-8601 with just the Date part, no time: needed for error messages
         */
        protected val DATE_FORMAT_STR_PLAIN = "yyyy-MM-dd"

        /**
         * This constant defines the date format specified by
         * RFC 1123 / RFC 822. Used for parsing via `SimpleDateFormat` as well as
         * error messages.
         */
        protected val DATE_FORMAT_STR_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz"

        /**
         * For error messages we'll also need a list of all formats.
         */
        protected val ALL_FORMATS = arrayOf(DATE_FORMAT_STR_ISO8601, "yyyy-MM-dd'T'HH:mm:ss.SSS", // ISO-8601 but no timezone
                DATE_FORMAT_STR_RFC1123, DATE_FORMAT_STR_PLAIN)

        /**
         * By default we use UTC for everything, with Jackson 2.7 and later
         * (2.6 and earlier relied on GMT)
         */
        val defaultTimeZone: TimeZone

        init {
            defaultTimeZone = TimeZone.getTimeZone("UTC") // since 2.7
        }

        protected val DEFAULT_LOCALE = Locale.US

        protected val DATE_FORMAT_RFC1123: DateFormat

        protected val DATE_FORMAT_ISO8601: DateFormat

        /* Let's construct "blueprint" date format instances: cannot be used
     * as is, due to thread-safety issues, but can be used for constructing
     * actual instances more cheaply (avoids re-parsing).
     */
        init {
            // Another important thing: let's force use of default timezone for
            // baseline DataFormat objects
            DATE_FORMAT_RFC1123 = SimpleDateFormat(DATE_FORMAT_STR_RFC1123, DEFAULT_LOCALE)
            DATE_FORMAT_RFC1123.timeZone = defaultTimeZone
            DATE_FORMAT_ISO8601 = SimpleDateFormat(DATE_FORMAT_STR_ISO8601, DEFAULT_LOCALE)
            DATE_FORMAT_ISO8601.timeZone = defaultTimeZone
        }

        /**
         * A singleton instance can be used for cloning purposes, as a blueprint of sorts.
         */
        val instance = JacksonStdDateFormat()

        /**
         * Blueprint "Calendar" instance for use during formatting. Cannot be used as is,
         * due to thread-safety issues, but can be used for constructing actual instances
         * more cheaply by cloning.
         *
         * @since 2.9.1
         */
        protected val CALENDAR: Calendar = GregorianCalendar(defaultTimeZone, DEFAULT_LOCALE)

        private fun pad2(buffer: StringBuffer, value: Int) {
            var value = value
            val tens = value / 10
            if (tens == 0) {
                buffer.append('0')
            } else {
                buffer.append(('0'.toInt() + tens).toChar())
                value -= 10 * tens
            }
            buffer.append(('0'.toInt() + value).toChar())
        }

        private fun pad3(buffer: StringBuffer, value: Int) {
            var value = value
            val h = value / 100
            if (h == 0) {
                buffer.append('0')
            } else {
                buffer.append(('0'.toInt() + h).toChar())
                value -= h * 100
            }
            pad2(buffer, value)
        }

        private fun pad4(buffer: StringBuffer, value: Int) {
            var value = value
            val h = value / 100
            if (h == 0) {
                buffer.append('0').append('0')
            } else {
                pad2(buffer, h)
                value -= 100 * h
            }
            pad2(buffer, value)
        }

        private fun _parse4D(str: String, index: Int): Int {
            return (1000 * (str[index] - '0')
                    + 100 * (str[index + 1] - '0')
                    + 10 * (str[index + 2] - '0')
                    + (str[index + 3] - '0'))
        }

        private fun _parse2D(str: String, index: Int): Int {
            return 10 * (str[index] - '0') + (str[index + 1] - '0')
        }

        private fun _cloneFormat(df: DateFormat, format: String,
                                 tz: TimeZone?, loc: Locale, lenient: Boolean?): DateFormat {
            var df = df
            if (loc != DEFAULT_LOCALE) {
                df = SimpleDateFormat(format, loc)
                df.setTimeZone(tz ?: defaultTimeZone)
            } else {
                df = df.clone() as DateFormat
                if (tz != null) {
                    df.timeZone = tz
                }
            }
            if (lenient != null) {
                df.isLenient = lenient
            }
            return df
        }

        protected fun <T> _equals(value1: T?, value2: T?): Boolean {
            return if (value1 === value2) {
                true
            } else value1 != null && value1 == value2
        }
    }
}