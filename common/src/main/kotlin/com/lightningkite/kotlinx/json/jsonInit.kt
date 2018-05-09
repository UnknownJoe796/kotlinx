package com.lightningkite.kotlinx.json

import com.lightningkite.kotlinx.locale.*
import kotlinx.serialization.SerialContext
import kotlinx.serialization.json.JSON

val MyJson = JSON(nonstrict = true, context = SerialContext()).apply{
    context!!.registerSerializer(Date::class, DateSerializer)
    context!!.registerSerializer(Time::class, TimeSerializer)
    context!!.registerSerializer(TimeStamp::class, TimeStampSerializer)
}