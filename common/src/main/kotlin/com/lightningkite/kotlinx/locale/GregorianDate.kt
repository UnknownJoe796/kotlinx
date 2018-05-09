package com.lightningkite.kotlinx.locale

data class GregorianDate(
        val year:Int,
        val month:Int,
        val day:Int
){
    object Year{
        const val fourYearsInDays:Int = 365*4 + 1
        fun isLeapYear(year:Int):Boolean
                = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
        fun days(year: Int) = if(isLeapYear(year)) 366 else 365
        fun monthLengths(year:Int) = if(isLeapYear(year)) monthLengthsLeapYear else monthLengths
        val monthLengths = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val monthLengthsLeapYear = intArrayOf(31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
    }
}

//private fun leapsSince1970(days:Int):Int{
//
//}
//
//fun Date.toGregorian():GregorianDate{
//    //Advance by year
//    var year = 1970
//    var daysLeft = daysSinceEpoch
//
//    while(daysLeft > GregorianDate.Year.days(year)){
//        daysLeft -= GregorianDate.Year.days(year)
//        year++
//    }
//
//    //Advance by day
//    val monthLengths = GregorianDate.Year.monthLengths(year)
//    for(month in monthLengths.indices){
//        val monthLength = monthLengths[month]
//        if(daysLeft < monthLength){
//            daysLeft
//        } else {
//            daysLeft -= monthLength
//        }
//    }
//}