package com.lightningkite.kotlinx.locale

import com.lightningkite.kotlinx.locale.TimeConstants

internal fun unleap(millisecondsSinceEpoch: Long): Long = millisecondsSinceEpoch - LeapSeconds.first { millisecondsSinceEpoch > it.first }.second
internal fun leap(millisecondsSinceEpoch: Long): Long = millisecondsSinceEpoch + LeapSeconds.first { millisecondsSinceEpoch > it.first }.second

internal val LeapSeconds = listOf(
        -Long.MAX_VALUE to 0L,   //0 ever more
        (365 * 2 + 0) * TimeConstants.MS_PER_DAY to 10000L,    // 1 Jan 1972
        (365 * 2 + 1 + 181) * TimeConstants.MS_PER_DAY to 11000L,    // 1 Jul 1972
        (365 * 3 + 1) * TimeConstants.MS_PER_DAY to 12000L,    // 1 Jan 1973
        (365 * 4 + 1) * TimeConstants.MS_PER_DAY to 13000L,    // 1 Jan 1974
        (365 * 5 + 1) * TimeConstants.MS_PER_DAY to 14000L,    // 1 Jan 1975
        (365 * 6 + 1) * TimeConstants.MS_PER_DAY to 15000L,    // 1 Jan 1976
        (365 * 7 + 2) * TimeConstants.MS_PER_DAY to 16000L,    // 1 Jan 1977
        (365 * 8 + 2) * TimeConstants.MS_PER_DAY to 17000L,    // 1 Jan 1978
        (365 * 9 + 2) * TimeConstants.MS_PER_DAY to 18000L,    // 1 Jan 1979
        (365 * 10 + 2) * TimeConstants.MS_PER_DAY to 19000L,    // 1 Jan 1980
        (365 * 11 + 3 + 181) * TimeConstants.MS_PER_DAY to 20000L,    // 1 Jul 1981
        (365 * 12 + 3 + 181) * TimeConstants.MS_PER_DAY to 21000L,    // 1 Jul 1982
        (365 * 13 + 3 + 181) * TimeConstants.MS_PER_DAY to 22000L,    // 1 Jul 1983
        (365 * 15 + 4 + 181) * TimeConstants.MS_PER_DAY to 23000L,    // 1 Jul 1985
        (365 * 18 + 4) * TimeConstants.MS_PER_DAY to 24000L,    // 1 Jan 1988
        (365 * 20 + 5) * TimeConstants.MS_PER_DAY to 25000L,    // 1 Jan 1990
        (365 * 21 + 5) * TimeConstants.MS_PER_DAY to 26000L,    // 1 Jan 1991
        (365 * 22 + 5 + 181) * TimeConstants.MS_PER_DAY to 27000L,    // 1 Jul 1992
        (365 * 23 + 5 + 181) * TimeConstants.MS_PER_DAY to 28000L,    // 1 Jul 1993
        (365 * 24 + 6 + 181) * TimeConstants.MS_PER_DAY to 29000L,    // 1 Jul 1994
        (365 * 26 + 6) * TimeConstants.MS_PER_DAY to 30000L,    // 1 Jan 1996
        (365 * 27 + 6 + 181) * TimeConstants.MS_PER_DAY to 31000L,    // 1 Jul 1997
        (365 * 29 + 7) * TimeConstants.MS_PER_DAY to 32000L,    // 1 Jan 1999
        (365 * 36 + 8) * TimeConstants.MS_PER_DAY to 33000L,    // 1 Jan 2006
        (365 * 39 + 9) * TimeConstants.MS_PER_DAY to 34000L,    // 1 Jan 2009
        (365 * 42 + 9 + 181) * TimeConstants.MS_PER_DAY to 35000L,    // 1 Jul 2012
        (365 * 45 + 10 + 181) * TimeConstants.MS_PER_DAY to 36000L,    // 1 Jul 2015
        (365 * 47 + 11) * TimeConstants.MS_PER_DAY to 37000L    // 1 Jan 2017
)